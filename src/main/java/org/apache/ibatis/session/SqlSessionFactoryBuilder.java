package org.apache.ibatis.session;


import org.apache.ibatis.build.XMLConfigBuilder;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;


/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/21
 * </p>
 */
public class SqlSessionFactoryBuilder {
    public SqlSessionFactory build(Reader reader) {
        return build(reader, null, null);
    }

    public SqlSessionFactory build(Reader reader, String environment, Properties properties) {
        try {
            XMLConfigBuilder parser = new XMLConfigBuilder(reader, environment, properties);
            Configuration configuration = parser.parse();
            return new DefaultSqlSessionFactory(configuration);
        } catch (Exception e) {
            throw new ResultException("Error building SqlSession.", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception ignore) {
            }
        }
    }

//    public SqlSessionFactory build(InputStream inputStream) {
//        return build(inputStream, null, null);
//    }
//
//    public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
//        try {
//            XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
//            Configuration configuration = parser.parse();
//            return new DefaultSqlSessionFactory(configuration);
//        } catch (Exception e) {
//            throw new ResultException("Error building SqlSession.", e);
//        } finally {
//            try {
//                if (inputStream != null) {
//                    inputStream.close();
//                }
//            } catch (Exception ignore) {
//            }
//        }
//    }
}
