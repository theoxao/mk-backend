package com.theoxao.commons.jdbc;

import java.util.*;


/**
 * Usage:
 * <ul>
 * <li>
 * <p>f.id > 1 and f.name like "%Jack%"</p>
 * <p>String keyword = "Jack";new Criteria().gt("f.id", 1).like("f.name", '%' + "keyword + '%')</p>
 * </li>
 * <li>
 * <p>f.id > 1 and ( f.age > 20 or f.gender = 'female' ) and f.birthday between beginDate and endDate</p>
 * <p>Criteria criteria = new Criteria().gt("f.id", 1);criteria.sub(true).gt("f.age", 20).eq("f.gender", "female");criteria.between("f.birthday", beginDate, endDate);</p>
 * </li>
 * </ul>
 */

public class Criteria {
    private int paramIndex;
    private int subCriteriaIndex;
    private String paramPrefix = "criteria_param_";
    /**
     * The logical of this Criteria, use <code>OR</code> when true, <code>AND</code> otherwise.
     */
    private boolean or;

    /**
     * Order by clause of this criteria, this field has no real effect, but just holding the string which can be used when building SQL string..
     */
    private String orderByClause;
    /**
     * Calculated where clause which this Criteria represents.
     */
    private String whereClause;

    private Map<String, Object> params;
    private List<Criterion> criterionList = new ArrayList<>();

    /**
     * Create a new Criteria, use <code>AND</code> logical as default.
     */
    public Criteria() {
        this(false);
    }

    /**
     * Create a new Criteria
     *
     * @param or Whether to use <code>OR</code> as the logical of this Criteria
     */
    public Criteria(boolean or) {
        this.or = or;
    }

    private Criteria(boolean or, int subCriteriaIndex) {
        this.or = or;
        this.paramPrefix += subCriteriaIndex + "_";
    }

    private String getProperty() {
        return paramPrefix + paramIndex++;
    }

    /**
     * Create a sub Criteria, use <code>AND</code> logical as default.
     */
    public Criteria sub() {
        return sub(false);
    }

    /**
     * Create a sub Criteria
     *
     * @param or Whether to use <code>OR</code> as the logical of this Criteria
     */
    public Criteria sub(boolean or) {
        Criteria subCriteria = new Criteria(or, subCriteriaIndex++);
        criterionList.add(new Criterion(subCriteria));
        return subCriteria;
    }

    /**
     * Set order by clause
     *
     * @param orderByClause The order by clause (without <code>ORDER BY</code> prefix)
     */
    public Criteria orderBy(String orderByClause) {
        if (orderByClause == null || orderByClause.indexOf(';') != -1 || orderByClause.indexOf('\'') != -1 || orderByClause.indexOf('"') != -1) {
            return this;
        }
        this.orderByClause = orderByClause;
        return this;
    }

    /**
     * Set order by clause with params, ie: ORDER BY Field(id, :ids)
     *
     * @param orderByClause The order by clause (without <code>ORDER BY</code> prefix)
     * @param params        params used by orderByClause
     */
    public Criteria orderBy(String orderByClause, Map<String, Object> params) {
        if (orderByClause == null || orderByClause.indexOf(';') != -1 || orderByClause.indexOf('\'') != -1 || orderByClause.indexOf('"') != -1) {
            return this;
        }
        this.orderByClause = orderByClause;
        if(this.params == null) {
            this.params = new HashMap<>();
        }
        this.params.putAll(params);
        return this;
    }

    /**
     * @return The order by clause this Criteria holds, prefix <code>ORDER BY</code> if it's not empty.
     */
    public String getOrderByClause() {
        return orderByClause == null || orderByClause.length() == 0 ? "" : " ORDER BY " + orderByClause;
    }

    /**
     * @param column the column of the condition, left hand
     * @param value  the value of the condition, right hand
     * @see Operation#eq
     */
    public Criteria eq(String column, Object value) {
        criterionList.add(new Criterion(column, Operation.eq, value, getProperty()));
        return this;
    }

    /**
     * @param column   the column of the condition, left hand
     * @param value    the value of the condition, right hand
     * @param function the function used to wrap the value
     * @see Operation#eq
     */
    public Criteria eq(String column, Object value, Function function) {
        criterionList.add(new Criterion(column, Operation.eq, value, getProperty(), function));
        return this;
    }

    /**
     * @param column the column of the condition, left hand
     * @param value  the value of the condition, right hand
     * @see Operation#ne
     */
    public Criteria ne(String column, Object value) {
        criterionList.add(new Criterion(column, Operation.ne, value, getProperty()));
        return this;
    }

    /**
     * @param column   the column of the condition, left hand
     * @param value    the value of the condition, right hand
     * @param function the function used to wrap the value
     * @see Operation#eq
     */
    public Criteria ne(String column, Object value, Function function) {
        criterionList.add(new Criterion(column, Operation.ne, value, getProperty(), function));
        return this;
    }

    /**
     * @param column
     * @param value
     * @see Operation#lt
     */
    public Criteria lt(String column, Object value) {
        criterionList.add(new Criterion(column, Operation.lt, value, getProperty()));
        return this;
    }

    /**
     * @param column
     * @param value
     * @param function
     * @see Operation#lt
     */
    public Criteria lt(String column, Object value, Function function) {
        criterionList.add(new Criterion(column, Operation.lt, value, getProperty(), function));
        return this;
    }

    /**
     * @param column
     * @param value
     * @see Operation#lte
     */
    public Criteria lte(String column, Object value) {
        criterionList.add(new Criterion(column, Operation.lte, value, getProperty()));
        return this;
    }

    /**
     * @param column
     * @param value
     * @param function
     * @see Operation#lte
     */
    public Criteria lte(String column, Object value, Function function) {
        criterionList.add(new Criterion(column, Operation.lte, value, getProperty(), function));
        return this;
    }

    /**
     * @param column
     * @param value
     * @see Operation#gt
     */
    public Criteria gt(String column, Object value) {
        criterionList.add(new Criterion(column, Operation.gt, value, getProperty()));
        return this;
    }

    /**
     * @param column
     * @param value
     * @param function
     * @see Operation#gt
     */
    public Criteria gt(String column, Object value, Function function) {
        criterionList.add(new Criterion(column, Operation.gt, value, getProperty(), function));
        return this;
    }

    /**
     * @param column
     * @param value
     * @see Operation#gte
     */
    public Criteria gte(String column, Object value) {
        criterionList.add(new Criterion(column, Operation.gte, value, getProperty()));
        return this;
    }

    /**
     * @param column
     * @param value
     * @param function
     * @see Operation#gte
     */
    public Criteria gte(String column, Object value, Function function) {
        criterionList.add(new Criterion(column, Operation.gte, value, getProperty(), function));
        return this;
    }

    /**
     * @param column
     * @param value
     * @see Operation#like
     */
    public Criteria like(String column, String value) {
        criterionList.add(new Criterion(column, Operation.like, value, getProperty()));
        return this;
    }

    /**
     * @param column
     * @param value
     * @see Operation#likes
     */
    public Criteria likes(String column, String value) {
        criterionList.add(new Criterion(column, Operation.likes, value, getProperty()));
        return this;
    }

    /**
     * @param column
     * @param value
     * @see Operation#in
     */
    public Criteria in(String column, Collection<?> value) {
        if (value.isEmpty()) {
            return this;
        }
        criterionList.add(new Criterion(column, Operation.in, value, getProperty()));
        return this;
    }

    /**
     * @param column
     * @see Operation#nullValue
     */
    public Criteria nullValue(String column) {
        criterionList.add(new Criterion(column, Operation.isNull));
        return this;
    }

    /**
     * @param column
     * @see Operation#notNull
     */
    public Criteria notNull(String column) {
        criterionList.add(new Criterion(column, Operation.isNotNull));
        return this;
    }

    /**
     * @param column
     * @param value
     * @param value2
     * @see Operation#between
     */
    public Criteria between(String column, Object value, Object value2) {
        criterionList.add(new Criterion(column, Operation.between, value, getProperty(), value2, getProperty()));
        return this;
    }

    /**
     * Get the generated where clause, must call {@link #toSql()} first
     */
    public String getWhereClause() {
        if (whereClause == null) {
            throw new RuntimeException("where clause is not generated!!");
        }
        return whereClause;
    }

    /**
     * Generate where clause SQL fragment, and return a map holding parameter and value entries.
     * This method only calculates once, all subsequent calls will immediately return the cached result, and modifications on the returned Map object will also reflect to the cached one.
     *
     * @see #toSql(Map)
     */
    public Map<String, Object> toSql() {
        // already calculated
        if (this.whereClause != null) {
            return params;
        }
        if (criterionList.isEmpty()) {
            whereClause = "";
            return Collections.emptyMap();
        }
        if(this.params == null) {
            this.params = new HashMap<>();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" WHERE ");
        toSql(sb, params);
        whereClause = sb.toString();
        return params;
    }

//    public Criteria and(String column, Operation operation, Object value) {
//        criterionList.add(new Criterion(column, operation, value, getProperty()));
//        return this;
//    }
//
//    public Criteria and(String column, Operation operation, Object value, Object value2) {
//        criterionList.add(new Criterion(column, operation, value, getProperty(), value2, getProperty()));
//        return this;
//    }
//
//    public Criteria and(String column, Operation operation) {
//        criterionList.add(new Criterion(column, operation));
//        return this;
//    }
//
//    public Criteria or() {
//        Criteria subCriteria = new Criteria(true, subCriteriaIndex++);
//        criterionList.add(new Criterion(subCriteria));
//        return subCriteria;
//    }

    /**
     * @return The where clause SQL fragment generated using the conditions this criteria holds.
     * @see #toSql(StringBuilder, Map)
     */
    public String toSql(Map<String, Object> params) {
        if (criterionList.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" WHERE ");
        toSql(sb, params);
        return sb.toString();
    }

    /**
     * Genreate the where clause SQL fragment using conditions held by this criteria
     *
     * @param sb     The output of the generated where clause SQL fragment
     * @param params The output of the parameter and value entries
     */
    private void toSql(StringBuilder sb, Map<String, Object> params) {
        boolean first = true;
        String logic = or ? " OR " : " AND ";
        for (Criterion criteria : criterionList) {
            if (first) {
                first = false;
            } else {
                sb.append(logic);
            }

            if (criteria.subCriteria != null) {
                sb.append("(");
                criteria.subCriteria.toSql(sb, params);
                sb.append(")");
                continue;
            }

            switch (criteria.operation) {
                case in:
                    sb.append(criteria.column).append(" ").append(criteria.operation.sql).append(" (:").append(criteria.property).append(")");
                    params.put(criteria.property, criteria.value);
                    break;
                case between:
                    sb.append(criteria.column).append(" ").append(criteria.operation.sql).append(" :").append(criteria.property).append(" AND :").append(criteria.property2);
                    params.put(criteria.property, criteria.value);
                    params.put(criteria.property2, criteria.value2);
                    break;
                case like:
                case likes:
                    sb.append(criteria.column).append(" ").append(criteria.operation.sql).append("(:").append(criteria.property).append(")");
                    params.put(criteria.property, criteria.value);
                    break;
                case isNull:
                case isNotNull:
                    sb.append(criteria.column).append(" ").append(criteria.operation.sql);
                    break;
                default:
                    if (criteria.function != null) {
                        sb.append(criteria.function.sql).append('(').append(criteria.column).append(") ").append(criteria.operation.sql).append(' ').append(criteria.function.sql).append("( :").append(criteria.property).append(')');
                    } else {
                        sb.append(criteria.column).append(" ").append(criteria.operation.sql).append(" :").append(criteria.property);
                    }
                    params.put(criteria.property, criteria.value);
                    break;
            }
        }
    }

    /**
     * SQL Operations
     */
    public enum Operation {
        in("IN"),
        lt("<"),
        gt(">"),
        eq("="),
        ne("!="),
        lte("<="),
        gte(">="),
        like("LIKE"),
        likes("LIKES"),
        isNull("IS NULL", 0),
        isNotNull("IS NOT NULL", 0),
        between("BETWEEN", 2);
        public final String sql;
        public final int argCount;

        Operation(String sql) {
            this.sql = sql;
            this.argCount = 1;
        }

        Operation(String sql, int argCount) {
            this.sql = sql;
            this.argCount = argCount;
        }
    }

    /**
     * SQL Functions
     */
    public enum Function {
        date("DATE");
        public final String sql;

        Function(String sql) {
            this.sql = sql;
        }
    }

    static class Criterion {

        String column;
        Object value;
        Object value2;
        String property;
        String property2;
        Function function;
        Operation operation;

        Criteria subCriteria;

        Criterion(Criteria subCriteria) {
            this.subCriteria = subCriteria;
        }

        Criterion(String column, Operation operation) {
            assert operation.argCount == 0;
            this.column = column;
            this.operation = operation;
        }

        Criterion(String column, Operation operation, Object value, String property) {
            assert operation.argCount == 1;
            this.column = column;
            this.operation = operation;
            this.value = value;
            this.property = property;
        }

        Criterion(String column, Operation operation, Object value, String property, Function function) {
            assert operation.argCount == 1;
            this.column = column;
            this.operation = operation;
            this.value = value;
            this.property = property;
            this.function = function;
        }

        Criterion(String column, Operation operation, Object value, String property, Object value2, String property2) {
            assert operation.argCount == 2;
            this.column = column;
            this.operation = operation;
            this.value = value;
            this.property = property;
            this.value2 = value2;
            this.property2 = property2;
        }


    }
}
