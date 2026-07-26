package jworkspace.weather.similarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import jworkspace.weather.model.Observation;

/**
 * A Vantage-Point Tree (VP-Tree) implementation designed to perform
 * logarithmic nearest-neighbor searches on spatial weather observations.
 */
public class ObservationVPTree {

    private static final Random RAND = new Random();
    private final Node root;

    /**
     * Constructs a multidimensional spatial search index over the collection of base photos.
     *
     * @param observations the collection of targeted photo observations
     */
    public ObservationVPTree(List<Observation> observations) {
        if (observations == null || observations.isEmpty()) {
            this.root = null;
        } else {
            this.root = buildTree(new ArrayList<>(observations));
        }
    }

    @SuppressWarnings("checkstyle:ReturnCount")
    private Node buildTree(List<Observation> list) {
        if (list.isEmpty()) {
            return null;
        }

        // Choose a random element as the vantage point pivot
        int index = RAND.nextInt(list.size());
        Observation vp = list.remove(index);
        Node node = new Node(vp);

        if (list.isEmpty()) {
            return node;
        }

        // Compute distances from the selected pivot point to all remaining elements
        double[] distances = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            distances[i] = getDistance(vp, list.get(i));
        }

        // Find the median distance to split internal nodes evenly
        double[] sortedDistances = distances.clone();
        Arrays.sort(sortedDistances);
        double median = sortedDistances[sortedDistances.length / 2];
        node.radius = median;

        List<Observation> leftChildList = new ArrayList<>();
        List<Observation> rightChildList = new ArrayList<>();

        // Distribute coordinates based on boundary constraints
        for (int i = 0; i < list.size(); i++) {
            if (distances[i] <= median) {
                leftChildList.add(list.get(i));
            } else {
                rightChildList.add(list.get(i));
            }
        }

        // Recursively build lower coordinate branches
        node.left = buildTree(leftChildList);
        node.right = buildTree(rightChildList);
        return node;
    }

    /**
     * Resolves high-dimensional scalar routing distances via your domain calculator.
     */
    private double getDistance(Observation o1, Observation o2) {
        Double d = ObservationSimilarityCalculator.between(o1, o2, SimilarityWeights.PHOTO);
        return d != null ? d : Double.MAX_VALUE;
    }

    /**
     * Executes an optimized O(log M) nearest neighbor search on the spatial graph.
     *
     * @param target the reference baseline observation row
     * @param k the maximum number of neighbors to retrieve
     * @param maxDistance the maximum allowable structural distance cut-off
     * @return a list of closest matching instances sorted from most similar to the least similar
     */
    public List<Observation> findNearest(Observation target, int k, double maxDistance) {
        List<Observation> results = new ArrayList<>();
        if (this.root == null || target == null || k <= 0) {
            return results;
        }

        // Max-Heap priority queue to track the closest elements discovered so far
        PriorityQueue<SearchResult> pq = new PriorityQueue<>(k,
            Comparator.comparingDouble((SearchResult res) -> res.distance).reversed()
        );

        search(this.root, target, k, maxDistance, pq);

        // Extract elements from the queue into a sequentially ordered list
        while (!pq.isEmpty()) {
            results.addFirst(pq.poll().observation);
        }
        return results;
    }

    private void search(Node node, Observation target, int k, double maxDistance, PriorityQueue<SearchResult> pq) {
        if (node == null) {
            return;
        }

        double dist = getDistance(target, node.observation);

        // Evaluate the current vantage node
        if (dist <= maxDistance) {
            if (pq.size() < k) {
                pq.add(new SearchResult(node.observation, dist));
            } else if (dist < pq.peek().distance) {
                pq.poll();
                pq.add(new SearchResult(node.observation, dist));
            }
        }

        // Determine the dynamic bounding sphere radius based on our heap capacity
        double bound = (pq.size() < k) ? maxDistance : pq.peek().distance;

        // Traverse down tree paths selectively, bypassing unaligned segments
        if (dist < node.radius) {
            if (dist - bound <= node.radius) {
                search(node.left, target, k, maxDistance, pq);
            }
            if (dist + bound >= node.radius) {
                search(node.right, target, k, maxDistance, pq);
            }
        } else {
            if (dist + bound >= node.radius) {
                search(node.right, target, k, maxDistance, pq);
            }
            if (dist - bound <= node.radius) {
                search(node.left, target, k, maxDistance, pq);
            }
        }
    }

    private static class Node {
        final Observation observation;
        double radius;
        Node left;
        Node right;

        Node(Observation observation) {
            this.observation = observation;
        }
    }

    private record SearchResult(Observation observation, double distance) {}
}


