package org.vitrivr.cineast.api.messages.result;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.List;
import org.vitrivr.cineast.api.messages.abstracts.AbstractQueryResultMessage;
import org.vitrivr.cineast.api.messages.interfaces.MessageType;
import org.vitrivr.cineast.core.data.entities.MediaObjectDescriptor;

/**
 * A {@link MediaObjectQueryResult} contains a list of {@link MediaObjectDescriptor}s as content of
 * the result message. It is part of a response for queries.
 *
 * @author rgasser
 * @version 1.0
 * @created 22.01.17
 */
public class MediaObjectQueryResult extends AbstractQueryResultMessage<MediaObjectDescriptor> {

  /**
   * Constructor for the SimilarityQueryResult object.
   *
   * @param queryId String representing the ID of the query to which this part of the result
   *                message.
   * @param content List of {@link MediaObjectDescriptor} of the objects belonging to to a query
   *                response.
   */
  @JsonCreator
  public MediaObjectQueryResult(String queryId, List<MediaObjectDescriptor> content) {
    super(queryId, MediaObjectDescriptor.class, content);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MessageType getMessageType() {
    return MessageType.QR_OBJECT;
  }

}
