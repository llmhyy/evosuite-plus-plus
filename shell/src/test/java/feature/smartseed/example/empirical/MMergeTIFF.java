package feature.smartseed.example.empirical;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MMergeTIFF extends Thread{
	   private static String TARGET_DIRECTORY = "T:/claimsexport/merged/";
	   private static String TIFF_DIR = "T:/claimsexport";
//	   private static SortedSet filesToMergeSet;
//	   private static Iterator fileToMergeIterator;
//	   private static HashMap documentsMap;
//	   private static MProperties properties = MProperties.getInstance();
//	   private static MLog logger = MLog.getInstance();

	   public MMergeTIFF(String name) {
	      super(name);
//	      TIFF_DIR = properties.getPropertyValue("image_dir");
//	      TARGET_DIRECTORY = properties.getPropertyValue("merged_dir");
	   }

	   public MMergeTIFF() {
//	      TIFF_DIR = properties.getPropertyValue("image_dir");
//	      TARGET_DIRECTORY = properties.getPropertyValue("merged_dir");
	   }
	   
	   public void saveMultiPageTif(RenderedImage[] image, String file) throws IOException {
		      String filename = file;
		      if (!file.endsWith(".tif")) {
		         filename = new String(file + ".tif");
		      }
		      List list = new ArrayList();
		      for(int i = 0; i < image.length; ++i) {
		          list.add(image[i]);
		       }
	   }
}
