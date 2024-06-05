package com.saite.dispatching_4.common.base.transform;

import org.hibernate.HibernateException;
import org.hibernate.property.access.internal.PropertyAccessStrategyBasicImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyChainedImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyFieldImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyMapImpl;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.transform.AliasToBeanResultTransformer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据库查询结果转为bean，包含父类属性
 * @author yangjian
 *
 */
public class AliasToBeanTransformer extends AliasToBeanResultTransformer {

	private static final long serialVersionUID = 3619677048891216250L;
	private final Class<?> resultClass;
	private boolean isInitialized;
	private String[] aliases;
	private Setter[] setters;
	private static Pattern linePattern = Pattern.compile("_(\\w)");

	public AliasToBeanTransformer(Class<?> resultClass) {
		super(resultClass);
		if (resultClass == null) {
			throw new IllegalArgumentException("resultClass cannot be null");
		}
		isInitialized = false;
		this.resultClass = resultClass;
	}

	@Override
	public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
		return false;
	}

	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		Object result;

		try {
			if (!isInitialized) {
				initialize(aliases);
			} else {
				check(aliases);
			}
			
			//获取父类所有属性
			
			result = resultClass.newInstance();

			for (int i = 0; i < aliases.length; i++) {
				if (setters[i] != null) {
					Object value=tuple[i];
					if(value instanceof BigInteger) {
						setters[i].set(result, ((BigInteger)value).longValue(), null);
					}else if(value instanceof BigDecimal) {
						setters[i].set(result, ((BigDecimal)value).floatValue(), null);
					}else if(value instanceof Timestamp){
						setters[i].set(result, Optional.ofNullable((Timestamp)value)
						        .map(Timestamp::toLocalDateTime)
						        .orElse(null), null);
					}else if(value instanceof Date){
						setters[i].set(result, Optional.ofNullable((Date)value)
						        .map(Date::toLocalDate)
						        .orElse(null), null);
					}else {
						setters[i].set(result,value, null);
					}
				}
			}
		} catch (InstantiationException e) {
			throw new HibernateException("Could not instantiate resultclass: " + resultClass.getName());
		} catch (IllegalAccessException e) {
			throw new HibernateException("Could not instantiate resultclass: " + resultClass.getName());
		}

		return result;
	}

	private void initialize(String[] aliases) {
		PropertyAccessStrategyChainedImpl propertyAccessStrategy = new PropertyAccessStrategyChainedImpl(
				PropertyAccessStrategyBasicImpl.INSTANCE, PropertyAccessStrategyFieldImpl.INSTANCE,
				PropertyAccessStrategyMapImpl.INSTANCE);
		this.aliases = new String[aliases.length];
		setters = new Setter[aliases.length];
		for (int i = 0; i < aliases.length; i++) {
			String alias = aliases[i];
			if (alias != null) {
				this.aliases[i] = alias;
				setters[i] = propertyAccessStrategy.buildPropertyAccess(resultClass, lineToHump(alias)).getSetter();
			}
		}
		isInitialized = true;
	}

	private void check(String[] aliases) {
		if (!Arrays.equals(aliases, this.aliases)) {
			throw new IllegalStateException("aliases are different from what is cached; aliases="
					+ Arrays.asList(aliases) + " cached=" + Arrays.asList(this.aliases));
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		AliasToBeanTransformer that = (AliasToBeanTransformer) o;

		if (!resultClass.equals(that.resultClass)) {
			return false;
		}
		if (!Arrays.equals(aliases, that.aliases)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = resultClass.hashCode();
		result = 31 * result + (aliases != null ? Arrays.hashCode(aliases) : 0);
		return result;
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
