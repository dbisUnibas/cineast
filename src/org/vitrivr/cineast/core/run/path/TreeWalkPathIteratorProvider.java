package org.vitrivr.cineast.core.run.path;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.core.run.ExtractionCompleteListener;
import org.vitrivr.cineast.core.run.ExtractionContextProvider;
import org.vitrivr.cineast.core.run.ExtractionPathProvider;
import org.vitrivr.cineast.core.util.LogHelper;

/*
 * Recursively add all files under that path to the List of files that should be processed. Uses
 * the context-provider to determine the depth of recursion, skip files and limit the number of
 * files.
 */
public class TreeWalkPathIteratorProvider implements ExtractionPathProvider,
    ExtractionCompleteListener {

  private static final Logger LOGGER = LogManager.getLogger();

  private final Path basePath;
  private final ExtractionContextProvider context;
  private volatile boolean open = true;
  private Iterator<Path> pathIterator = Collections.emptyIterator();

  public TreeWalkPathIteratorProvider(Path basePath, ExtractionContextProvider context) {
    this.basePath = basePath;
    this.context = context;
    try {
      pathIterator = Files.walk(this.basePath, this.context.depth(), FileVisitOption.FOLLOW_LINKS)
          .filter(p -> {
            try {
              return Files.exists(p) && Files.isRegularFile(p) && !Files.isHidden(p)
                  && Files.isReadable(p);
            } catch (IOException e) {
              LOGGER.error("An IO exception occurred while testing the media file at '{}': {}",
                  p.toString(),
                  LogHelper.getStackTrace(e));
              return false;
            }
          }).iterator();
    } catch (IOException e) {
      LOGGER.error("An IO exception occurred while scanning '{}': {}", basePath.toString(),
          LogHelper.getStackTrace(e));
    }
  }

  @Override
  public void close() {
    open = false;
  }

  @Override
  public void addPaths(List<Path> pathList) {
    LOGGER.error("Cannot add paths to a TreeWalkPathIterator");
  }

  /**
   * Since no elements are added to the iterator, this provider is also closed when the iterator does not have further elements.
   */
  @Override
  public boolean isOpen() {
    return pathIterator.hasNext() && open;
  }

  @Override
  public boolean hasNextAvailable() {
    return pathIterator.hasNext() && open;
  }

  @Override
  public synchronized Optional<Path> next() {
    if (pathIterator.hasNext() && open) {
      return Optional.of(pathIterator.next());
    }
    return Optional.empty();
  }

  @Override
  public void onCompleted(Path path) {
    //Ignore for Files
  }
}