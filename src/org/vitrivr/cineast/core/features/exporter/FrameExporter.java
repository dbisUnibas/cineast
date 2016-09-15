package org.vitrivr.cineast.core.features.exporter;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.core.config.Config;
import org.vitrivr.cineast.core.data.Frame;
import org.vitrivr.cineast.core.data.SegmentContainer;
import org.vitrivr.cineast.core.db.PersistencyWriterSupplier;
import org.vitrivr.cineast.core.features.extractor.Extractor;
import org.vitrivr.cineast.core.setup.EntityCreator;
import org.vitrivr.cineast.core.util.LogHelper;

public class FrameExporter implements Extractor {

	private File folder = new File(Config.getExtractorConfig().getOutputLocation(), "exportedFrames");
	private int offset;
	private String format = "png";
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public FrameExporter(int offsetBetweenFrames){
		this.offset = offsetBetweenFrames;
	}
	
	public FrameExporter(int offsetBetweenFrames, String format){
		this(offsetBetweenFrames);
		this.format = format;
	}
	
	@Override
	public void init(PersistencyWriterSupplier phandlerSupply) {
		if(!this.folder.exists()){
			this.folder.mkdirs();
		}
	}

	@Override
	public void processShot(SegmentContainer shot) {
		for(Frame f : shot.getFrames()){
			if(f.getId() % this.offset == 0){
				try {
					ImageIO.write(f.getImage().getBufferedImage(), this.format, new File(folder, String.format("%06d",(f.getId() / this.offset)) + "." + this.format));
				} catch (IOException e) {
					LOGGER.error("Error while exporting frame: {}", LogHelper.getStackTrace(e));
				}
			}
		}
	}

	@Override
	public void finish() {}

	@Override
	public void initalizePersistentLayer(Supplier<EntityCreator> supply) {}

}
