package kxlive.gjrlibrary.db;

import android.content.ContentValues;
import android.database.Cursor;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * 用于给业务对象绑定表对象的相关操作
 *
 * @param <T>
 * @author gjr
 */
public class DataBinder<T> {

    public static DataBinder binder=new DataBinder();

    public void save(T table) {
        ((DataSupport) table).save();

    }

    public void saveThrows(T table) {
        ((DataSupport) table).saveThrows();

    }

    public int update(Class<T> modelClass, ContentValues values, long id) {
        return DataSupport.update(modelClass, values, 2);

    }

    public int updateAll(Class<T> modelClass, ContentValues values,
                         String... conditions) {
        return DataSupport.updateAll(modelClass, values, conditions);
        /*
		 * 把表中title为“今日iPhone6发布”且评论数量大于0的所有新闻的title改成“今日iPhone6 Plus发布”
		 * 就是sqlite的查询语句写法要求一致，占位符的方式来实现条件约束 ContentValues values = new
		 * ContentValues(); values.put("title", "今日iPhone6 Plus发布");
		 * DataSupport.updateAll(News.class, values,
		 * "title = ? and commentcount > ?", "今日iPhone6发布", "0");
		 * 如果不写约束条件，则最后会将所有行的title修改为“今日iPhone6发布”
		 * updateAll()方法在不指定约束条件的情况下就是修改所有行的数据
		 */

    }

    public int update(T table, long id) {
        return ((DataSupport) table).update(id);

    }

    public int update(T table, String... conditions) {
        return ((DataSupport) table).updateAll(conditions);

    }

    public void setToDefault(T table, String fieldName) {
        ((DataSupport) table).setToDefault(fieldName);
    }

    public int delete(Class<T> modelClass, long id) {
        return DataSupport.delete(modelClass, 2);

    }

    public int deleteAll(Class<T> modelClass, String... conditions) {
        return DataSupport.deleteAll(modelClass, conditions);

    }

    public int deleteAll(Class<T> modelClass) {
        return DataSupport.deleteAll(modelClass);

    }

    public int delete(T table) {
        return ((DataSupport) table).delete();

    }

    public int delete(String tablename, String... conditions) {
        return DataSupport.deleteAll(tablename, conditions);

    }

    public boolean isSave(T table) {
        return ((DataSupport) table).isSaved();

    }

    public T find(Class<T> modelClass, int index) {
        return DataSupport.find(modelClass, index);

    }

    public T findFirst(Class<T> modelClass) {
        return DataSupport.findFirst(modelClass);

    }

    public T findLast(Class<T> modelClass) {
        return DataSupport.findLast(modelClass);

    }

    public List<T> findAll(Class<T> modelClass, long[] ids) {
        return DataSupport.findAll(modelClass, ids);

    }

    public List<T> findAll(Class<T> modelClass) {
        return DataSupport.findAll(modelClass);

    }

    public Cursor findAll(String sql) {
        return DataSupport.findBySQL(sql);

    }

    public List<T> findWithSelects(Class<T> modelClass, String[] selects) {

        List<T> resultList = DataSupport.select(selects).find(modelClass);

        return resultList;

    }

    public List<T> findWithWhere(Class<T> modelClass, String... where) {

        List<T> resultList = DataSupport.where(where).find(modelClass);

        return resultList;

    }

    public List<T> findWithCluster(Class<T> modelClass, String[] selects,
                                   String... where) {

        List<T> resultList = DataSupport.select(selects).where(where)
                .find(modelClass);

        return resultList;

    }

    public List<T> findWithCluster(Class<T> modelClass, String[] selects,
                                   String[] where, String order) {

        List<T> resultList = DataSupport.select(selects).where(where)
                .order(order).find(modelClass);

        return resultList;

    }

    public List<T> findWithCluster(Class<T> modelClass, String[] selects,
                                   String[] where, String order, int limit) {

        List<T> resultList = DataSupport.select(selects).where(where)
                .order(order).limit(limit).find(modelClass);

        return resultList;

    }

    public List<T> findWithCluster(Class<T> modelClass, String[] selects,
                                   String[] where, String order, int limit, int offset) {

		/*
		 * 连缀查询:只能是查询到指定表中的数据而已，关联表中数据是无法查到的 List<T> newsList =
		 * DataSupport.select("title", "content") .where("commentcount > ?",
		 * "0") //where约束条件 .order("publishdate desc")//排序 .limit(10)//前10条数量限制
		 * .offset(10) //偏移量10 .find(modelClass);
		 */

        List<T> resultList = DataSupport.select(selects).where(where)
                .order(order).limit(limit).offset(offset).find(modelClass);
        return resultList;

    }

    public List<T> findWithCluster(Class<T> modelClass, String[] selects,
                                   String[] where, String order, int limit, int offset, boolean isEager) {

		/*
		 * 连缀查询:只能是查询到指定表中的数据而已，关联表中数据是无法查到的 LitePal默认的模式就是懒查询，当然这也是推荐的查询方式
		 * 每一个类型的find()方法，都对应了一个带有isEager参数的方法重载
		 * 设置成true就表示激进查询，这样就会把关联表中的数据一起查询出来 这会将和本表关联的所有表中的数据也一起查出来
		 * 但是这种查询方式LitePal并不推荐，因为如果一旦关联表中的数据很多，查询速度可能就会非常慢。
		 * 而且激进查询只能查询出指定表的关联表数据，但是没法继续迭代查询关联表的关联表数据。
		 */

        List<T> resultList = DataSupport.select(selects).where(where)
                .order(order).limit(limit).offset(offset)
                .find(modelClass, isEager);

        return resultList;

    }

    public int count(Class<T> modelClass) {

        return DataSupport.count(modelClass);

    }

    public int sum(Class<?> modelClass, String columnName, Class<Integer> columnType) {

        return DataSupport.sum(modelClass, columnName, columnType);

    }

    public int sumWhere(Class<?> modelClass, String columnName, Class<Integer> columnType, String... where) {

        return DataSupport.where(where).sum(modelClass, columnName, columnType);

    }

    public double average(Class<T> modelClass, String columnName) {

        return DataSupport.average(modelClass, columnName);

    }

    public T max(Class<?> modelClass, String columnName, Class<T> columnType) {

        return DataSupport.max(modelClass, columnName, columnType);

    }

    public T min(Class<?> modelClass, String columnName, Class<T> columnType) {

        return DataSupport.min(modelClass, columnName, columnType);

    }


}
