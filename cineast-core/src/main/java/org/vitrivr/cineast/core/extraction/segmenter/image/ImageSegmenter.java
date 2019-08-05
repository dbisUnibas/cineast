package org.vitrivr.cineast.core.extraction.segmenter.image;

import org.vitrivr.cineast.core.config.ImageCacheConfig;
import org.vitrivr.cineast.core.data.MultiImageFactory;
import org.vitrivr.cineast.core.data.segments.ImageSegment;
import org.vitrivr.cineast.core.data.segments.SegmentContainer;
import org.vitrivr.cineast.core.extraction.ExtractionContextProvider;
import org.vitrivr.cineast.core.extraction.segmenter.general.PassthroughSegmenter;
import org.vitrivr.cineast.core.extraction.segmenter.general.Segmenter;
import org.vitrivr.cineast.core.util.ReflectionHelper;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * A {@link Segmenter} that converts a {@link BufferedImage} into a {@link SegmentContainer}.
 *
 * @see Segmenter
 * @see PassthroughSegmenter
 *
 * @author rgasser
 * @version 1.0
 */
public class ImageSegmenter extends PassthroughSegmenter<BufferedImage> {
    /** THe {@link MultiImageFactory} that is used to create image segments. */
    private final MultiImageFactory factory;

    /**
     * Constructor for {@link ImageSegmenter required for instantiation through {@link ReflectionHelper }.
     *
     * @param context The {@link ExtractionContextProvider } for the extraction context this {@link ImageSegmenter} is created in.
     */
    public ImageSegmenter(ExtractionContextProvider context) {
        super();
        this.factory = context.imageCache().sharedMultiImageFactory();
    }

    /**
     * Constructor for {@link ImageSegmenter required for instantiation through {@link ReflectionHelper }.
     *
     * @param context The {@link ExtractionContextProvider} for the extraction context this {@link ImageSegmenter} is created in.
     * @param properties A HashMap containing the configuration properties for {@link ImageSegmenter}
     */
    public ImageSegmenter(ExtractionContextProvider context, Map<String,String> properties) {
        super();
        this.factory = new MultiImageFactory(context.imageCache());
    }

    /**
     * Creates a new {@link SegmentContainer} from a {@link BufferedImage}.
     *
     * @param content The {@link BufferedImage} to extract a {@link SegmentContainer} from.
     * @return {@link SegmentContainer}
     */
    @Override
    protected SegmentContainer getSegmentFromContent(BufferedImage content) {
        return new ImageSegment(content, this.factory);
    }
}
