package org.vitrivr.cineast.api.messages.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.core.data.query.containers.QueryContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * @author rgasser
 * @version 1.0
 * @created 11.01.17
 */
public class QueryComponent {

  /**
   * List of {@link QueryTerm}s in this {@link QueryComponent}.
   */
  private final List<QueryTerm> terms;

  /**
   * The client-generated uuid for this container for reference purposes
   */
  private final String containerId;

  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * Constructor for QueryComponent.
   */
  @JsonCreator
  public QueryComponent(@JsonProperty("terms") List<QueryTerm> terms, @JsonProperty("containerId") String containerId) {
    this.terms = terms;
    this.containerId = containerId;
  }

  /**
   * Getter for terms.
   *
   * @return List of QueryTerms
   */
  public List<QueryTerm> getTerms() {
    return this.terms;
  }

  /**
   * Converts the provided collection of QueryComponent objects to a map that maps feature categories defined in the query-terms to @{@link QueryContainer} derived from the {@link QueryTerm}.
   *
   * @return Category map.
   */
  public static HashMap<String, ArrayList<QueryContainer>> toCategoryMap(Collection<QueryComponent> components) {
    final HashMap<String, ArrayList<QueryContainer>> categoryMap = new HashMap<>();
    if (components.isEmpty()) {
      LOGGER.warn("Empty components collection, returning empty map");
      return categoryMap;
    }
    for (QueryComponent component : components) {
      if (component.getTerms().isEmpty()) {
        LOGGER.warn("No terms for component {}", component);
        continue;
      }
      for (QueryTerm term : component.getTerms()) {
        if (term.getCategories().isEmpty()) {
          LOGGER.warn("No categories for term {}", term);
        }
        for (String category : term.getCategories()) {
          if (!categoryMap.containsKey(category)) {
            categoryMap.put(category, new ArrayList<>());
          }
          final QueryContainer container = term.toContainer();
          if (container != null) {
            container.setId(component.containerId);
            categoryMap.get(category).add(container);
          } else {
            LOGGER.warn("Null container generated for term {}", term);
          }
        }
      }
    }
    return categoryMap;
  }

  /**
   * Converts the provided collection of {@link QueryComponent} object to a map of {@link QueryContainer} and their categories.
   *
   * @return A map of querycontainers with their associated categories
   */
  public static HashMap<QueryContainer, List<String>> toContainerMap(Collection<QueryComponent> components){
    final HashMap<QueryContainer, List<String>> map = new HashMap<>();
    if(components.isEmpty()){
      LOGGER.warn("Empty components collection, returning empty list of containers");
    }else{
      for(QueryComponent component: components){
        for(QueryTerm qt : component.getTerms()){
          map.put(qt.toContainer(), qt.getCategories());
        }
      }
    }
    return map;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
