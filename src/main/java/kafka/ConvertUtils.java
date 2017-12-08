package kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import log.DataLogger;
import org.apache.avro.Schema;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.log4j.Logger;
import util.JsonMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * ConvertUtils class.
 *
 * @author jantezana*
 * @version 2017/07/07
 */
public class ConvertUtils {

    /**
     * Converts to byte stream
     *
     * @param data the data
     * @return the byte stream
     */
    public static <T extends SpecificRecordBase> byte[] convertToByteStream(T data, Class<T> clazz) {
        Logger log = Logger.getLogger(ConvertUtils.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Schema schema = data.getSchema();
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        DatumWriter<T> writer = new SpecificDatumWriter<>(schema);
        try {
            writer.write(data, encoder);
            encoder.flush();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            log.error(String.format("io.error.convert.byte.stream\n[%s]", e.getMessage()));
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends SpecificRecordBase> byte[] jsonMessageToBytes(String jsonObject, Class<T> clazz) {
        byte[] bytes = new byte[0];

        try {
            final T t = JsonMapper.getObjectFromJsonString(jsonObject, clazz);
            if (t != null) {
                bytes = ConvertUtils.convertToByteStream(t, clazz);
            }
        } catch (IOException e) {
            DataLogger.error(ConvertUtils.class, "Error at getting bytes of Class");
        }

        return bytes;
    }

    public static <T extends SpecificRecordBase> byte[] jsonMessageToBytes(JsonNode jsonObject, Class<T> clazz) {
        byte[] bytes = new byte[0];
        try {
            final T t = JsonMapper.getObjectFromJson(jsonObject, clazz);
            if (t != null) {
                bytes = ConvertUtils.convertToByteStream(t, clazz);
            }
        } catch (IOException e) {
            DataLogger.error(ConvertUtils.class, "Error at getting bytes of Class");
        }

        return bytes;
    }
}
