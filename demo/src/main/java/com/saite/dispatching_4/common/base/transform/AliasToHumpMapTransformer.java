package com.saite.dispatching_4.common.base.transform;

import org.hibernate.transform.AliasedTupleSubsetResultTransformer;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * map驼峰式转换器
 * 
 * @author yangjian
 *
 */
public class AliasToHumpMapTransformer extends AliasedTupleSubsetResultTransformer {
	private static final long serialVersionUID = 2141161832143806461L;
	private static Pattern linePattern = Pattern.compile("_(\\w)");

	public static final AliasToHumpMapTransformer INSTANCE = new AliasToHumpMapTransformer();

	/**
	 * Disallow instantiation of AliasToEntityMapResultTransformer.
	 */
	private AliasToHumpMapTransformer() {
	}

	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		Map<String,Object> result = new HashMap<>(tuple.length);
		for (int i = 0; i < tuple.length; i++) {
			String alias = aliases[i];
			Object value=tuple[i];
			if (alias != null) {
				if(value instanceof Timestamp){
					result.put(lineToHump(alias), Optional.ofNullable((Timestamp)value)
					        .map(Timestamp::toLocalDateTime)
					        .orElse(null));
				}else if(value instanceof Date){
					result.put(lineToHump(alias), Optional.ofNullable((Date)value)
					        .map(Date::toLocalDate)
					        .orElse(null));
				}else {
					result.put(lineToHump(alias), value);
				}
			}
		}
		return result;
	}

	@Override
	public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
		return false;
	}

	/**
	 * Serialization hook for ensuring singleton uniqueing.
	 *
	 * @return The singleton instance : {@link #INSTANCE}
	 */
	private Object readResolve() {
		return INSTANCE;
	}

	/** 下划线转驼峰 */
	private String lineToHump(String str) {
//		str = str.toLowerCase();
		Matcher matcher = linePattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

}
