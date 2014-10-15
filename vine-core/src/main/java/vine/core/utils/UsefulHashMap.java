package vine.core.utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Created by liguofang on 2014/10/10.
 * 扩展数据获取方法的LinkedhashMap
 * @author liguofang
 */
public class UsefulHashMap<K, V> extends LinkedHashMap<K, V> {
        private static final long serialVersionUID = 6076144240738135034L;
        private static final Logger log = LoggerFactory.getLogger(UsefulHashMap.class);

        /**
         * 获取字符串值
         * @param key
         * @return
         */
        public String getString(String key) {
            if (key == null) return null;
            Object value = this.get(key);
            if (value == null) return null;
            return value.toString();
        }

        /**
         * 获取字符串值
         * @param key
         * @param defValue
         * @return
         */
        public String getString(String key, String defValue) {
            String result = defValue;
            if (key == null) return result;
            Object value = this.get(key);
            if (value == null) return result;
            if (value.toString().equals("")) return result;
            result = value.toString();
            return result;
        }

        /**
         * 获取Int值
         * @param key
         * @return
         */
        public Integer getInteger(String key) {
            if (key == null) return null;
            Object value = this.get(key);
            if (value == null) return null;
            Integer result = 0;
            if (value instanceof String) {
                try {
                    result = Integer.parseInt(value.toString());
                } catch (Exception e) {
                    result = 0;
                }
            } else if (value instanceof Integer ||
                    value instanceof Long ||
                    value instanceof Float ||
                    value instanceof Double) {
                result = (Integer) value;
            } else {
                result = 0;
            }
            return result;
        }

        /**
         * 获取Int值
         * @param key
         * @param defValue
         * @return
         */
        public Integer getInteger(String key, Integer defValue) {
            Integer result = defValue;
            if (key == null) return result;
            Object value = this.get(key);
            if (value == null) return result;
            if (value instanceof String) {
                try {
                    result = Integer.parseInt(value.toString());
                } catch (Exception e) {
                    result = defValue;
                }
            } else if (value instanceof Integer ||
                    value instanceof Long ||
                    value instanceof Float ||
                    value instanceof Double) {
                result = (Integer) value;
            } else {
                result = defValue;
            }
            return result;
        }

        /**
         * 获取Int值
         * @param key
         * @return
         */
        public int getIntValue(String key) {
            if (key == null) return 0;
            Object value = this.get(key);
            if (value == null) return 0;
            int result = 0;
            if (value instanceof String) {
                try {
                    result = Integer.parseInt(value.toString());
                } catch (Exception e) {
                    result = 0;
                }
            } else if (value instanceof Integer ||
                    value instanceof Long ||
                    value instanceof Float ||
                    value instanceof Double) {
                result = ((Integer) value).intValue();
            } else {
                result = 0;
            }
            return result;
        }

        /**
         * 获取Int值
         * @param key
         * @param defValue
         * @return
         */
        public int getIntValue(String key, int defValue) {
            Integer result = defValue;
            if (key == null) return result;
            Object value = this.get(key);
            if (value == null) return result;
            if (value instanceof String) {
                try {
                    result = Integer.parseInt(value.toString());
                } catch (Exception e) {
                    result = defValue;
                }
            } else if (value instanceof Integer ||
                    value instanceof Long ||
                    value instanceof Float ||
                    value instanceof Double) {
                result = ((Integer) value).intValue();
            } else {
                result = defValue;
            }
            return result;
        }

        /**
         * 获取Long值
         * @param key
         * @return
         */
        public Long getLong(String key) {
            if (key == null) return null;
            Object value = this.get(key);
            if (value == null) return null;
            Long result = 0L;
            if (value instanceof String) {
                try {
                    result = Long.parseLong(value.toString());
                } catch (Exception e) {
                    result = 0L;
                }
            } else if (value instanceof Integer ||
                    value instanceof Long ||
                    value instanceof Float ||
                    value instanceof Double) {
                result = (Long) value;
            } else {
                result = 0L;
            }
            return result;
        }

        /**
         * 获取Long值
         * @param key
         * @param defValue
         * @return
         */
        public Long getLong(String key, Long defValue) {
            Long result = defValue;
            if (key == null) return result;
            Object value = this.get(key);
            if (value == null) return result;
            if (value instanceof String) {
                try {
                    result = Long.parseLong(value.toString());
                } catch (Exception e) {
                    result = defValue;
                }
            } else if (value instanceof Integer ||
                    value instanceof Long ||
                    value instanceof Float ||
                    value instanceof Double) {
                result = (Long) value;
            } else {
                result = defValue;
            }
            return result;
        }

        /**
         * 获取Float值
         * @param key
         * @return
         */
        public Float getFloat(String key) {
            if (key == null) return 0f;
            Object value = this.get(key);
            if (value == null) return 0f;
            Float result = 0f;
            if (value instanceof String) {
                try {
                    result = Float.parseFloat(value.toString());
                } catch (Exception e) {
                    result = 0f;
                }
            } else if (value instanceof Integer ||
                    value instanceof Long ||
                    value instanceof Float ||
                    value instanceof Double) {
                result = (Float) value;
            } else {
                result = 0f;
            }
            return result;
        }

        /**
         * 获取Float值
         * @param key
         * @param defValue
         * @return
         */
        public Float getFloat(String key, Float defValue) {
            Float result = defValue;
            if (key == null) return result;
            Object value = this.get(key);
            if (value == null) return result;
            if (value instanceof String) {
                try {
                    result = Float.parseFloat(value.toString());
                } catch (Exception e) {
                    result = defValue;
                }
            } else if (value instanceof Integer ||
                    value instanceof Long ||
                    value instanceof Float ||
                    value instanceof Double) {
                result = (Float) value;
            } else {
                result = defValue;
            }
            return result;
        }

        /**
         * 获取boolean值
         * @param key
         * @return
         */
        public boolean getBoolean(String key) {
            if (key == null) return false;
            Object value = this.get(key);
            if (value == null) return false;
            boolean result = false;
            if (value instanceof String) {
                try {
                    result = Boolean.parseBoolean(value.toString());
                } catch (Exception e) {
                    result = false;
                }
            } else if (value instanceof Boolean) {
                result = (Boolean) value;
            } else {
                result = false;
            }
            return result;
        }

        /**
         * 获取boolean值
         * @param key
         * @param defValue
         * @return
         */
        public boolean getBoolean(String key, boolean defValue) {
            boolean result = defValue;
            if (key == null) return result;
            Object value = this.get(key);
            if (value == null) return result;
            if (value instanceof String) {
                try {
                    result = Boolean.parseBoolean(value.toString());
                } catch (Exception e) {
                    result = defValue;
                }
            } else if (value instanceof Boolean) {
                result = (Boolean) value;
            } else {
                result = defValue;
            }
            return result;
        }

        /**
         * 获取一个对象
         * @param key
         * @param clazz
         * @return
         */
        public <T> T getObject(String key, Class<T> clazz){
            Object obj = this.get(key);
            if (obj == null) {
                return null;
            }
            try {
                return castObject(obj, clazz);
            } catch (ClassCastException ex) {
                log.error("获取数据失败，key：" + key, ex);
            }
            return null;
        }

        private <T> T castObject(Object obj, Class<T> clazz) {
            Class objClazz = obj.getClass();
            Class subClazz = objClazz.asSubclass(clazz);
            return (T) obj;
        }

        /**
         * 获取对象集合
         * @param key
         * @param clazz
         * @return
         */
        public <T> List<T> getList(String key, Class<T> clazz){
            List<T> result = new ArrayList<T>();
            Object obj = this.get(key);
            if (obj == null) {
                return null;
            }
            if (obj instanceof Collection) {
                Collection coll = (Collection) obj;
                for (Object o : coll) {
                    try {
                        result.add(castObject(o, clazz));
                    } catch (ClassCastException ex) {
                        log.error("获取数据失败，key：" + key, ex);
                    }
                }
            }
            return result;
        }
}
