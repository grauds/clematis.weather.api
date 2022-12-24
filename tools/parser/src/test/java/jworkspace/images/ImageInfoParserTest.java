package jworkspace.images;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999 - 2022 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This application is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this application; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/
import java.io.IOException;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import org.junit.jupiter.api.Test;

/**
 * Test image EXIF extractors
 */
public class ImageInfoParserTest {

    @Test
    public void extractFromTestJpeg() throws ImageProcessingException, IOException {

        Metadata metadata = ImageMetadataReader
                .readMetadata(ImageInfoParserTest.class.getResourceAsStream("20191216_060646475_iOS.jpg"));

        debugPrintoutOfDirectories(metadata);
    }

    @Test
    public void extractFromHeic() throws ImageProcessingException, IOException {
        Metadata metadata = ImageMetadataReader
                .readMetadata(ImageInfoParserTest.class.getResourceAsStream("2022-07-11 09-58-21.HEIC"));

        debugPrintoutOfDirectories(metadata);
    }

    private void debugPrintoutOfDirectories(Metadata metadata) {
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                System.out.println(tag);
            }
        }
    }
}
