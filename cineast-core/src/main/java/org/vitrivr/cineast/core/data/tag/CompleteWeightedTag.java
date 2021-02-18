package org.vitrivr.cineast.core.data.tag;

import java.util.Objects;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CompleteWeightedTag implements WeightedTag {

  private final String id, name, description;
  private final Preference preference;
  private final float weight;

  public CompleteWeightedTag(String id, String name, String description, float weight, Preference preference) {
    this.id = id;
    this.name = name;
    this.description = (description == null) ? "" : description;
    this.weight = weight;
    this.preference = preference;
    if (this.id == null) {
      throw new NullPointerException("id cannot be null");
    }

    if (this.id.isEmpty()) {
      throw new IllegalArgumentException("id cannot be empty");
    }

    if (this.name == null) {
      throw new NullPointerException("name cannot be null");
    }

    if (this.name.isEmpty()) {
      throw new IllegalArgumentException("name cannot be empty");
    }

    if (this.weight > 1f || this.weight < 0f) {
      throw new IllegalArgumentException("weight " + this.weight + " outside of range (0,1)");
    }

  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public boolean hasId() {
    return true;
  }

  @Override
  public boolean hasName() {
    return true;
  }

  @Override
  public boolean hasDescription() {
    return !this.description.isEmpty();
  }

  @Override
  public boolean hasPreference() {
    return this.preference != null;
  }

  @Override
  public Preference getPreference() {
    return preference;
  }


  public float getWeight() {
    return this.weight;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, description, weight, preference);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    CompleteWeightedTag other = (CompleteWeightedTag) obj;
    if (description == null) {
      if (other.description != null) {
        return false;
      }
    } else if (!description.equals(other.description)) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (Float.floatToIntBits(weight) != Float.floatToIntBits(other.weight)) {
      return false;
    }
    return true;
  }


  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
  }

}
