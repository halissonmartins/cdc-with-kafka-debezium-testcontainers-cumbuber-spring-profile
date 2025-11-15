package br.com.halisson;

import static br.com.halisson.Constants.TZ_AMERICA_SAO_PAULO;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerReplicationHandler {
	
	private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = Constants.FROM_TOPIC_NAME, groupId = "replication-customer-group")
    public void receive(ConsumerRecord<String, String> consumerRecord) {

    	String value = consumerRecord.value();
        log.info("received payload={}", value);
        
        Optional<String> optionalJsonTrasnformed = transform(value); 
        
        if(optionalJsonTrasnformed.isPresent()) {
			log.info("Sending data={}", optionalJsonTrasnformed.get());
	        
			kafkaTemplate.send(Constants.TO_TOPIC_NAME, optionalJsonTrasnformed.get());
        }
    }

	private Optional<String> transform(String jsonBefore) {
        
        ObjectMapper mapper = new ObjectMapper();

        // Convert the string JSON in tree manipulable
		try {
			JsonNode rootNode = mapper.readTree(jsonBefore);
	        ObjectNode schemaNode = (ObjectNode) rootNode.get("schema");
	        ObjectNode payloadNode = (ObjectNode) rootNode.get("payload");
	
	        // 1️ Add the field "updated_at" in payload
	        LocalDateTime nowLocalDateTime = LocalDateTime.now(ZoneId.of(TZ_AMERICA_SAO_PAULO));
	        log.info("nowLocalDateTime: {}", nowLocalDateTime.toString()); 
	        
			payloadNode.put("updated_at", 
	        		nowLocalDateTime.atZone(
	        				ZoneId.of(TZ_AMERICA_SAO_PAULO)).toInstant().toEpochMilli());
	
	        // 2️ Add the field "created_at" in definition schema
	        ArrayNode fieldsArray = (ArrayNode) schemaNode.get("fields");
	
	        ObjectNode createdAtField = mapper.createObjectNode();
	        createdAtField.put("field", "updated_at");
	        createdAtField.put("name", "org.apache.kafka.connect.data.Timestamp");
	        createdAtField.put("optional", false);
	        createdAtField.put("type", "int64");
	        createdAtField.put("version", 1);
	
	        fieldsArray.add(createdAtField);
	
	        //3️ Reorder the keys 
	        ObjectNode newRoot = mapper.createObjectNode();
	        newRoot.set("payload", payloadNode);
	        newRoot.set("schema", schemaNode);
	
	        //4️ Converts back to JSON String
	        String jsonAfter = mapper.writeValueAsString(newRoot);
	
			return Optional.of(jsonAfter);
		
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
			
			return Optional.empty();
		}
        
	}

}
