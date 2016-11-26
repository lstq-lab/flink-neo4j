package org.apache.flink.streaming.connectors.neo4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.connectors.neo4j.mapper.Neo4JSourceMappingStrategy;
import org.apache.flink.streaming.connectors.neo4j.mapper.SerializationMapper;
import org.junit.Ignore;
import org.junit.Test;

public class Neo4JSourceTest implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_URL = "bolt://localhost:7687";

	private static final String DEFAULT_USERNAME = "neo4j";

	private static final String DEFAULT_PASSWORD = "password";

	@Ignore
	@Test
	public void testSource() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		Map<String, String> config = new HashMap<String, String>();
		config.put(Neo4JDriverWrapper.URL, DEFAULT_URL);
		config.put(Neo4JDriverWrapper.USERNAME_PARAM, DEFAULT_USERNAME);
		config.put(Neo4JDriverWrapper.PASSWORD_PARAM, DEFAULT_PASSWORD);

		SerializationMapper<String> serializationMapper = new StringSerializationMapper();
		String statement = "MERGE (tuple:Tuple {name: {t1}, index: {t2}}) RETURN tuple";

		Neo4JSourceMappingStrategy<String, SerializationMapper<String>> mappingStrategy = new Neo4JSourceMappingStrategy<String, SerializationMapper<String>>(
				statement, serializationMapper);

		Neo4JSourceMock<String> sourceMock = new Neo4JSourceMock<String>(mappingStrategy, config);
		DataStreamSource<String> dataStreamSource = env.addSource(sourceMock);
		dataStreamSource.addSink(new PrintSinkFunction<String>());

		env.execute();
	}

	class StringSerializationMapper implements SerializationMapper<String> {

		private static final long serialVersionUID = 1L;

		@Override
		public String serialize(Map<String, Object> record) {
			return record.get("description").toString();
		}
	}
}
