package com.kevin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysLog {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sys_log.id
     *
     * @mbggenerated Tue Sep 11 11:19:45 CST 2018
     */
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sys_log.type
     *
     * @mbggenerated Tue Sep 11 11:19:45 CST 2018
     */
    private Integer type;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sys_log.target_id
     *
     * @mbggenerated Tue Sep 11 11:19:45 CST 2018
     */
    private Integer targetId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sys_log.operator
     *
     * @mbggenerated Tue Sep 11 11:19:45 CST 2018
     */
    private String operator;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sys_log.operate_time
     *
     * @mbggenerated Tue Sep 11 11:19:45 CST 2018
     */
    private Date operateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sys_log.operate_ip
     *
     * @mbggenerated Tue Sep 11 11:19:45 CST 2018
     */
    private String operateIp;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sys_log.status
     *
     * @mbggenerated Tue Sep 11 11:19:45 CST 2018
     */
    private Integer status;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sys_log.id
     *
     * @return the value of sys_log.id
     *
     * @mbggenerated Tue Sep 11 11:19:45 CST 2018
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sys_log.id
     *
     * @param id the value for sys_log.id
     *
     * @mbggenerated Tue Sep 11 11:19:45 CST 2018
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sys_log.type
     *
     * @return the value of sys_log.type
     *
     * @mbggenerated Tue Sep 11 11:19:45 CST 2018
     */
    public Integer getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sys_log.type
     *
     * @param type the value for sys_log.type
     *
     * @mbggenerated Tue Sep 11 11:19:45 CST 2018
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sys_log.target_id
     *
     * @return the value of sys_log.target_id
     *
     * @mbggenerated Tue Sep 11 11:19:45 CST 2018
     */
    public Integer getTargetId() {
        return targetId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sys_log.target_id
     *
     * @param targetId the value for sys_log.target_id
     *
     * @mbggenerated Tue Sep 11 11:19:45 CST 2018
     */
    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sys_log.operator
     *
     * @return the value of sys_log.operator
     *
     * @mbggenerated Tue Sep 11 11:19:45 CST 2018
     */
    public String getOperator() {
        return operator;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sys_log.operator
     *
     * @param operator the value for sys_log.operator
     *
     * @mbggenerated Tue Sep 11 11:19:45 CST 2018
     */
    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sys_log.operate_time
     *
     * @return the value of sys_log.operate_time
     *
     * @mbggenerated Tue Sep 11 11:19:45 CST 2018
     */
    public Date getOperateTime() {
        return operateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sys_log.operate_time
     *
     * @param operateTime the value for sys_log.operate_time
     *
     * @mbggenerated Tue Sep 11 11:19:45 CST 2018
     */
    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sys_log.operate_ip
     *
     * @return the value of sys_log.operate_ip
     *
     * @mbggenerated Tue Sep 11 11:19:45 CST 2018
     */
    public String getOperateIp() {
        return operateIp;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sys_log.operate_ip
     *
     * @param operateIp the value for sys_log.operate_ip
     *
     * @mbggenerated Tue Sep 11 11:19:45 CST 2018
     */
    public void setOperateIp(String operateIp) {
        this.operateIp = operateIp == null ? null : operateIp.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sys_log.status
     *
     * @return the value of sys_log.status
     *
     * @mbggenerated Tue Sep 11 11:19:45 CST 2018
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sys_log.status
     *
     * @param status the value for sys_log.status
     *
     * @mbggenerated Tue Sep 11 11:19:45 CST 2018
     */
    public void setStatus(Integer status) {
        this.status = status;
    }
}