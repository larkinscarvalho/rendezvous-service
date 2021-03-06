// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import org.sdo.rendezvous.model.types.PKEPIDEnc;
import org.sdo.rendezvous.model.types.PKNull;
import org.sdo.rendezvous.model.types.PKRMEEnc;
import org.sdo.rendezvous.model.types.PKX509Enc;
import org.sdo.rendezvous.model.types.PubKey;
import org.sdo.rendezvous.model.types.PublicKeyEncoding;

public class PubKeyDeserializer extends JsonDeserializer<PubKey> {

  private static final int PK_ENC_INDEX = 1;
  private static final ImmutableMap<PublicKeyEncoding, Class<? extends PubKey>> PUBKEY_SUBTYPES =
      ImmutableMap.of(
          PublicKeyEncoding.NONE, PKNull.class,
          PublicKeyEncoding.X509, PKX509Enc.class,
          PublicKeyEncoding.RSAMODEXP, PKRMEEnc.class,
          PublicKeyEncoding.EPID, PKEPIDEnc.class);

  @Override
  public PubKey deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {

    ObjectCodec objectCodec = jsonParser.getCodec();
    JsonNode jsonNode = objectCodec.readTree(jsonParser);
    PublicKeyEncoding publicKeyEncoding =
        PublicKeyEncoding.valueOf(jsonNode.get(PK_ENC_INDEX).asInt());
    ObjectMapper mapper = new ObjectMapper();

    if (PUBKEY_SUBTYPES.containsKey(publicKeyEncoding)) {
      return mapper.readValue(jsonNode.toString(), PUBKEY_SUBTYPES.get(publicKeyEncoding));
    } else {
      throw new JsonParseException(
          jsonParser,
          "Couldn't find implementation for public key encoding: " + publicKeyEncoding.name());
    }
  }
}
