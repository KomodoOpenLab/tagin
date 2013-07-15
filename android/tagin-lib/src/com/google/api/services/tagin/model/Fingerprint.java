/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2013-06-26 16:27:34 UTC)
 * on 2013-06-30 at 15:17:19 UTC 
 * Modify at your own risk.
 */

package com.google.api.services.tagin.model;

/**
 * Model definition for Fingerprint.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the . For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Fingerprint extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Pattern pattern;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String urn;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public Fingerprint setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Pattern getPattern() {
    return pattern;
  }

  /**
   * @param pattern pattern or {@code null} for none
   */
  public Fingerprint setPattern(Pattern pattern) {
    this.pattern = pattern;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getUrn() {
    return urn;
  }

  /**
   * @param urn urn or {@code null} for none
   */
  public Fingerprint setUrn(java.lang.String urn) {
    this.urn = urn;
    return this;
  }

  @Override
  public Fingerprint set(String fieldName, Object value) {
    return (Fingerprint) super.set(fieldName, value);
  }

  @Override
  public Fingerprint clone() {
    return (Fingerprint) super.clone();
  }

}
