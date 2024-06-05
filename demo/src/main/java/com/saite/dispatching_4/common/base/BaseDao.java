package com.saite.dispatching_4.common.base;

import cn.hutool.core.collection.CollUtil;
import com.saite.dispatching_4.common.base.transform.AliasToBeanTransformer;
import com.saite.dispatching_4.common.base.transform.AliasToHumpMapTransformer;
import com.saite.dispatching_4.common.bean.page.PageData;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.internal.NativeQueryImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 数据操作基类
 * 
 * @author yangjian
 * @param <T>
 * @param <ID>
 */
@SuppressWarnings("unchecked")
public abstract class BaseDao<T, ID extends Serializable> {

	/**批量操作数据*/
	private final Integer BATCH_SIZE = 500;

	private final Class<T> tClass;
	@PersistenceContext
	private EntityManager em;

	public BaseDao() {
		this.tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public EntityManager getEntityManager() {
		return em;
	}

	/**
	 * 保存
	 */
	public void save(T t) {
		em.persist(t);
	}

	/**
	 * 批量保存
	 * @param var 集
	 */
	public void batchSave(Iterable<T> var) {
		Iterator<T> iterator = var.iterator();
		int index = 0;
		while (iterator.hasNext()) {
			em.persist(iterator.next());
			index++;
			if (index % BATCH_SIZE == 0) {
				em.flush();
				em.clear();
			}
		}
		if (index % BATCH_SIZE != 0) {
			em.flush();
			em.clear();
		}
	}

	/**
	 * 更新
	 *
	 * @param t
	 */
	public void update(T t) {
		em.merge(t);
	}

	/**
	 * 批量保存
	 * @param var 集
	 */
	public void batchUpdate(Iterable<T> var) {
		Iterator<T> iterator = var.iterator();
		int index = 0;
		while (iterator.hasNext()) {
			em.merge(iterator.next());
			index++;
			if (index % BATCH_SIZE == 0) {
				em.flush();
				em.clear();
			}
		}
		if (index % BATCH_SIZE != 0) {
			em.flush();
			em.clear();
		}
	}

	/**
	 * 删除
	 */
	public void delete(T t) {
		em.remove(t);
	}

	/**
	 * 批量删除
	 * @param var 差部署过
	 */
	public void batchDelete(Iterable<T> var) {
		Iterator<T> iterator = var.iterator();
		int index = 0;
		while (iterator.hasNext()) {
			em.remove(em.merge(iterator.next()));
			index++;
			if (index % BATCH_SIZE == 0) {
				em.flush();
				em.clear();
			}
		}
		if (index % BATCH_SIZE != 0) {
			em.flush();
			em.clear();
		}
	}

	/**
	 * 根据主键查询
	 */
	public T find(ID id) {
		return em.find(tClass, id);
	}


	/**
	 * 根据sql查询，使用占位符?的方式
	 * @return map类型list
	 */
	protected List<Map<String, Object>> findMapBySql(String sql, Object... args) {
		Query query = em.createNativeQuery(sql);
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				query.setParameter(i + 1, args[i]);
			}
		}
		query.unwrap(NativeQueryImpl.class).setResultTransformer(AliasToHumpMapTransformer.INSTANCE);
		return query.getResultList();
	}

	protected List<Map<String, Object>> listMapBySql(String sql, Object... args) {
		Session session = em.unwrap(Session.class);
		NativeQuery query = session.createNativeQuery(sql);
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				query.setParameter(i + 1, args[i]);
			}
		}
		query.setResultTransformer(AliasToHumpMapTransformer.INSTANCE);
		return query.getResultList();
	}

	/**
	 * 根据sql查询，使用占位符?的方式
	 * @return bean类型list
	 */
	@SuppressWarnings("deprecation")
	protected List<T> findBySql(String sql, Object... args) {
		Query query = em.createNativeQuery(sql);
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				query.setParameter(i + 1, args[i]);
			}
		}
		query.unwrap(NativeQuery.class).setResultTransformer(new AliasToBeanTransformer(tClass));
		return query.getResultList();
	}

	/**
	 * 根据sql查询，使用:name的方式
	 * @return map类型list
	 */
	protected List<Map<String, Object>> findMapBySql(String sql, Map<String, Object> args) {
		Query query = em.createNativeQuery(sql);
		if (args != null) {
			args.forEach(query::setParameter);
		}
		query.unwrap(NativeQueryImpl.class).setResultTransformer(AliasToHumpMapTransformer.INSTANCE);
		return query.getResultList();
	}

	protected List<Map<String, Object>> listMapBySql(String sql, Map<String, Object> args) {
		Session session = em.unwrap(Session.class);
		NativeQuery query = session.createNativeQuery(sql);
		if (args != null) {
			args.forEach(query::setParameter);
		}
		query.setResultTransformer(AliasToHumpMapTransformer.INSTANCE);
		return query.getResultList();
	}

	/**
	 * 根据sql查询，使用:name的方式
	 * @return bean类型list
	 */
	@SuppressWarnings("deprecation")
	protected List<T> findBySql(String sql, Map<String, Object> args) {
		Query query = em.createNativeQuery(sql);
		if (args != null) {
			args.forEach(query::setParameter);
		}
		query.unwrap(NativeQuery.class).setResultTransformer(new AliasToBeanTransformer(tClass));
		return query.getResultList();
	}

	/**
	 * 分页查询,:name方式
	 * @return 数据类型为map的分页对象
	 */
	protected <D> List<D> listBySql(String sql, Map<String, Object> args, Class<D> dClass) {
		Query query = em.createNativeQuery(sql);
		if (args != null) {
			args.forEach(query::setParameter);
		}
		query.unwrap(NativeQuery.class).setResultTransformer(new AliasToBeanTransformer(dClass));
		return query.getResultList();
	}

	protected <R> R findOne(String sql, Map<String, Object> args, Class<R> clazz) {
		Query q = em.createNativeQuery(sql);
		if (CollUtil.isNotEmpty(args)) {
			args.forEach(q::setParameter);
		}
		q.setMaxResults(1);
		q.unwrap(NativeQuery.class).setResultTransformer(new AliasToBeanTransformer(clazz));
		if (CollUtil.isEmpty(q.getResultList())) {
			return null;
		}
		return (R) q.getSingleResult();
	}

	/**
	 * 分页查询,占位符?方式
	 * @return 数据类型为map的分页对象
	 */
	protected <V> PageData<V> findPageBySql(PageData<V> pageData, Class<V> clazz, String sql, Object... args) {
		Query query = em.createNativeQuery(sql);
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				query.setParameter(i + 1, args[i]);
			}
		}
		query.unwrap(NativeQueryImpl.class).setResultTransformer(new AliasToBeanTransformer(clazz));
		query.setFirstResult(pageData.getPageSize() * (pageData.getPageNum() - 1));
		query.setMaxResults(pageData.getPageSize());
		String countSql = "select count(*) from (" + sql + ") countx";
		Object countObj = findSingleResult(countSql, args);
		int count = ((BigInteger) countObj).intValue();
		pageData.setTotalData(count);
		if (count != 0) {
			if (count % pageData.getPageSize() == 0) {
				pageData.setPageCount(count / pageData.getPageSize());
			} else {
				pageData.setPageCount(count / pageData.getPageSize() + 1);
			}
			pageData.setRows(query.getResultList());
		} else {
			pageData.setPageCount(0);
			pageData.setRows(Collections.emptyList());
		}
		return pageData;
	}

	/**
	 * 分页查询,占位符?方式
	 * @return 数据类型为map的分页对象
	 */
	protected PageData<Map<String, Object>> findMapPageBySql(PageData<Map<String, Object>> pageData, String sql, Object... args) {
		Query query = em.createNativeQuery(sql);
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				query.setParameter(i + 1, args[i]);
			}
		}
		query.unwrap(NativeQueryImpl.class).setResultTransformer(AliasToHumpMapTransformer.INSTANCE);
		query.setFirstResult(pageData.getPageSize() * (pageData.getPageNum() - 1));
		query.setMaxResults(pageData.getPageSize());
		String countSql = "select count(*) from (" + sql + ") countx";
		Object countObj = findSingleResult(countSql, args);
		int count = ((BigInteger) countObj).intValue();
		pageData.setTotalData(count);
		if (count != 0) {
			if (count % pageData.getPageSize() == 0) {
				pageData.setPageCount(count / pageData.getPageSize());
			} else {
				pageData.setPageCount(count / pageData.getPageSize() + 1);
			}
			pageData.setRows(query.getResultList());
		} else {
			pageData.setPageCount(0);
			pageData.setRows(Collections.emptyList());
		}
		return pageData;
	}

	/**
	 * 分页查询,:name方式
	 * @return 数据类型为map的分页对象
	 */
	protected PageData<Map<String, Object>> findMapPageBySql(PageData<Map<String, Object>> pageData, String sql, Map<String, Object> args) {
		Query query = em.createNativeQuery(sql);
		if (args != null) {
			args.forEach(query::setParameter);
		}
		query.unwrap(NativeQueryImpl.class).setResultTransformer(AliasToHumpMapTransformer.INSTANCE);
		query.setFirstResult(pageData.getPageSize() * (pageData.getPageNum() - 1));
		query.setMaxResults(pageData.getPageSize());
		String countSql = "select count(*) from (" + sql + ") countx";
		Object countObj = findSingleResult(countSql, args);
		int count = ((BigInteger) countObj).intValue();
		pageData.setTotalData(count);
		if (count != 0) {
			if (count % pageData.getPageSize() == 0) {
				pageData.setPageCount(count / pageData.getPageSize());
			} else {
				pageData.setPageCount(count / pageData.getPageSize() + 1);
			}
			pageData.setRows(query.getResultList());
		} else {
			pageData.setPageCount(0);
			pageData.setRows(Collections.emptyList());
		}
		return pageData;
	}

	/**
	 * 分页查询,:name方式
	 * @return 数据类型为map的分页对象
	 */
	protected <D> PageData<D> PageBySql(PageData<D> pageData, String sql, Map<String, Object> args, Class<D> dClass) {
		Query query = em.createNativeQuery(sql);
		if (args != null) {
			args.forEach(query::setParameter);
		}
		query.unwrap(NativeQuery.class).setResultTransformer(new AliasToBeanTransformer(dClass));
		query.setFirstResult(pageData.getPageSize() * (pageData.getPageNum() - 1));
		query.setMaxResults(pageData.getPageSize());
		String countSql = "select count(*) from (" + sql + ") countx";
		Object countObj = findSingleResult(countSql, args);
		int count = ((BigInteger) countObj).intValue();
		pageData.setTotalData(count);
		if (count != 0) {
			if (count % pageData.getPageSize() == 0) {
				pageData.setPageCount(count / pageData.getPageSize());
			} else {
				pageData.setPageCount(count / pageData.getPageSize() + 1);
			}
			pageData.setRows(query.getResultList());
		} else {
			pageData.setPageCount(0);
			pageData.setRows(Collections.emptyList());
		}
		return pageData;
	}

	/**
	 * 分页查询,占位符?方式
	 * @return 数据类型为bean的分页对象
	 */
	@SuppressWarnings("deprecation")
	protected PageData<T> findPageBySql(PageData<T> pageData, String sql, Map<String,Object> args) {
		Query query = em.createNativeQuery(sql);
		if (args != null) {
			args.forEach(query::setParameter);
		}
		query.unwrap(NativeQuery.class).setResultTransformer(new AliasToBeanTransformer(tClass));
		query.setFirstResult(pageData.getPageSize() * (pageData.getPageNum() - 1));
		query.setMaxResults(pageData.getPageSize());
		String countSql = "select count(*) from (" + sql + ") countx";
		Object countObj = findSingleResult(countSql, args);
		int count = ((BigInteger) countObj).intValue();
		pageData.setTotalData(count);
		if (count != 0) {
			if (count % pageData.getPageSize() == 0) {
				pageData.setPageCount(count / pageData.getPageSize());
			} else {
				pageData.setPageCount(count / pageData.getPageSize() + 1);
			}
			pageData.setRows(query.getResultList());
		} else {
			pageData.setPageCount(0);
			pageData.setRows(Collections.emptyList());
		}
		return pageData;
	}

	/**
	 * 分页查询,占位符?方式
	 * @return 数据类型为bean的分页对象
	 */
	@SuppressWarnings("deprecation")
	protected PageData<T> findPageBySql(PageData<T> pageData, String sql, Object... args) {
		Query query = em.createNativeQuery(sql);
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				query.setParameter(i + 1, args[i]);
			}
		}
		query.unwrap(NativeQuery.class).setResultTransformer(new AliasToBeanTransformer(tClass));
		query.setFirstResult(pageData.getPageSize() * (pageData.getPageNum() - 1));
		query.setMaxResults(pageData.getPageSize());
		String countSql = "select count(*) from (" + sql + ") countx";
		Object countObj = findSingleResult(countSql, args);
		int count = ((BigInteger) countObj).intValue();
		pageData.setTotalData(count);
		if (count != 0) {
			if (count % pageData.getPageSize() == 0) {
				pageData.setPageCount(count / pageData.getPageSize());
			} else {
				pageData.setPageCount(count / pageData.getPageSize() + 1);
			}
			pageData.setRows(query.getResultList());
		} else {
			pageData.setPageCount(0);
			pageData.setRows(Collections.emptyList());
		}
		return pageData;
	}

	/**
	 * 查询单个结果
	 */
	protected Object findSingleResult(String sql, Object... args) {
		Query query = em.createNativeQuery(sql);
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				query.setParameter(i + 1, args[i]);
			}
		}
		return query.getSingleResult();
	}

	/**
	 * 查询单个结果
	 */
	protected Object findSingleResult(String sql, Map<String, Object> args) {
		Query query = em.createNativeQuery(sql);
		if (args != null) {
			args.forEach(query::setParameter);
		}
		return query.getSingleResult();
	}

	/**
	 * 执行更新操作
	 * @return 执行成功的记录数
	 */
	protected int executeUpdate(String sql, Object... args) {
		Query query = em.createNativeQuery(sql);
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				query.setParameter(i + 1, args[i]);
			}
		}
		return query.executeUpdate();
	}

	/**
	 * 执行更新参数，参数使用Map
	 * @return 执行成功的记录数
	 */
	protected int executeUpdate(String sql, Map<String, Object> args) {
		Query query = em.createNativeQuery(sql);
		if (args != null) {
			args.forEach(query::setParameter);
		}
		return query.executeUpdate();
	}

	/**
	 * 生成in查询的占位符语句 ，(?,?,?,?)
	 *
	 * @param size 需要的问号个数
	 * @return (?, ?, ?, ?)
	 */
	public String buildInPlaceHoldersClause(int size) {
		return " (" + String.join(",", Collections.nCopies(size, "?")) + ") ";
	}

}
