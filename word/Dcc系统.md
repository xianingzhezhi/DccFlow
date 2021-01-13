# 										==Dcc系统==

## 一， 表结构

flow_his_sign

flow_base_files

bak_principal

bflow_sign_off

bak_user

flow_base_role

flow_base_user

bak_info_file2

flow_base_email

flow_his_logs

flow_base_select

flow_base_url

flow_base_stage

flow_base_router

flow_base_menu

flow_maintain_indicate

bak_cpl

flow_base_signprocess

flow_base_fileinfo

bflow_message

bflow_addpeople

| 表名                   | 备注                      | 老表 |
| ---------------------- | ------------------------- | ---- |
| flow_maintain_isofiles | 最大文件表                |      |
| flow_his_dcc_files     | 文件信息表（…查询事件表） |      |
| bak_info_file2         |                           |      |
| bflow_sign_off         |                           |      |
| bflow_rej_off          |                           |      |
| flow_agent             | 代理表                    |      |
| flow_base_dept         | 部门表                    |      |
| bak_user               | 用户表                    |      |
|                        |                           |      |
|                        |                           |      |
|                        |                           |      |

## 二，ISO

### ==保存==

关联表（flow_maintain_isofiles，flow_his_dcc_files，flow_base_signoff表，flow_his_signoff）

1. 根据user_id查询登陆用户信息,没有找到返回（當前用戶信息未維護）

2. 接收前台传入的表单信息isoForm，

3. 根据前台传入的form_id是否为空，

   ```
   if（form_id为空）{
   	根据file_name在flow_his_dcc_files表中查询是否存在，
   	if（存在）{
   		返回（文件名已存在,請知悉!），
   	}
   	生成系统单号
   	生成文件编号
   	签核流程表（flow_base_signoff）插入一笔数据
   }else{
   	根据form_id，以及开立状态在flow_his_signoff表中查找数据
   	if(不为空){
   		根据form_id，以及开立状态在flow_base_signoff表中查找数据
   		if(flow_base_signoff表中的user_id与登录user_id不一致){
   			返回(駁回單據,應由申請者修改,操作失敗！)
   		}
   		根据form_id在flow_maintain_isofiles表中查找数据
   		if(有数据){
   			判断该数据的状态
   			if（为1）{
   				返回（此單據正在送核中，不能修改，操作失敗！）
   			}
   			if（为2）{
   				返回（此單據已結案,不可操作！）
   			}
   			if（为3）{
   				返回（此文件編號已填寫超過30天未送簽,已被佔用不可以操作！）
   			}
   			if(前台传入的isoForm的file_id首字母与file_code1不一致 || 前台传入的isoForm的				file_id第二个字母与file_code2不一致){
   				if(操作标识符为2或3，作废、变更){
   					根据form_id删除flow_maintain_isofiles信息
   					根据form_id删除fflow_base_signoff信息
   					返回（此文件不是外來文件，此單據已刪除！）
   				}else{
   				    file_code1，file_code2根据生成文件编号
   				}
   			}
   		}
   	}
   
   }
   ```









### **==ISO文件作废流程==**

#### 1，…查询事件（flow_his_dcc_files表）

根据* @param fistId   文件前缀ISO/OSO
* @param propertyName  数据库表属性
* @param input   输入框内容，查询条件
* @param symbol   符号:  例如<  <=  >  >=  =  like

```
if (input!=""&& input!=null){
            input=input.replace("*", "%");
        }
        String sql = "SELECT file_id, version,  file_name FROM flow_system.flow_his_dcc_files where flag='Y'";
        if (fistId !=null && fistId !=""){
            if (fistId.equals("OSO")){
                sql =sql + " and file_id like 'O%'";
            } else if (fistId.equals("ISO")) {
                sql = sql + " and file_id not like 'O%'";
            }
        }
        if(propertyName !=null && propertyName !=""){
            if ( (input != null && input != "") && (symbol != null && symbol != "")){
                sql = sql + " and "+propertyName+" "+symbol + "'"+input+"'";
            }
        }
        //排序
        sql= sql + "  ORDER BY file_id";
```

#### 2，确定按钮

关联表（flow_maintain_isofiles，flow_his_dcc_files）

1. 根据 file_id,  file_name, version在flow_his_dcc_files表中查询文件是否存在，不存在，返回（沒有找到此文件信息,請重新輸入）
2. 根据file_id,  file_name在flow_maintain_isofiles表中查询状态在（0，1）中的数据，如果查询到，返回（此文件還有簽核未結案不可作廢），否则（进入作废原因页面）

#### 3，作废原因页面

关联表（flow_maintain_isofiles，bak_info_file2，bflow_sign_off）

1. 从大文件表（flow_maintain_isofiles）中 获取最新的数据的from_id
2.  根据最新的from_id号查询 FILE2（bak_info_file2）信息
3.  获去登录用户信息（根据前台传入的user_id）
4.  生成一个(flow_maintain_isofiles)文件编号大 form_id
5. 将生成的 文件编号大 以及user信息，文件信息，作废原因插入到flow_maintain_isofiles表中
6.  将生成的 文件编号大 以及上面获得的FILE2信息插入到File2（bak_info_file2）表中
7.  将申请信息插入到文件变化记录表(bflow_sign_off)
8.  最后跳转到ISO文件新增页面

### **==ISO文件变更==**

####  1，...查询按钮复用作废里面的查询接口

#### 2，确定按钮

关联表（flow_his_dcc_files，flow_maintain_isofiles）

1. 根据 file_id,  file_name, version在flow_his_dcc_files表中查询文件是否存在，不存在，返回（沒有找到此文件信息,請重新輸入）
2. 根据file_id,  file_name在flow_maintain_isofiles表中查询状态不再在2（结案）中的数据，如果查询到，返回（此文件還有簽核未結案不可变更），否则（进入变更原因页面），备注：只有结案的文件才能变更

#### 3，文件变更原因（和作废相似）

关联表（flow_maintain_isofiles，bak_info_file2，bflow_sign_off）

不同：版本号升级，插入到（flow_maintain_isofiles）信息不同（版本升级），插入到文件变化记录表（bflow_sign_off）信息不同

1. 从大文件表（flow_maintain_isofiles）中 获取最新的数据的from_id
2.  根据最新的from_id号查询 FILE2（bak_info_file2）信息
3.  获去登录用户信息（根据前台传入的user_id）
4.  生成一个(flow_maintain_isofiles)文件编号大 form_id
5. 将文件版本号升级一个，例如：A-->B
6. 将生成的 文件编号大 ,新的版本号，以及user信息，文件信息，变更原因插入到flow_maintain_isofiles表中
7.  将生成的 文件编号大 以及上面获得的FILE2信息插入到File2（bak_info_file2）表中
8.  将申请信息插入到文件变化记录表(bflow_sign_off)
9.  最后跳转到ISO文件新增页面

### ==ISO文件变更通知单==

流程：

关联表（flow_maintain_isofiles，bak_info_file2，bflow_sign_off）

1. 根据 file_id,  file_name, version在flow_his_dcc_files表中查询文件是否存在，不存在，返回（無此文件Dcc文件）
2. 根据file_id,  file_name在flow_maintain_isofiles表中查询状态在（0，1）中的数据，如果查询到，返回（此文件還有簽核未結案）
3. 获去登录用户信息（根据前台传入的user_id）
4. 发送邮件

### ==ISO抽单==

流程：

关联表（flow_maintain_isofiles，bflow_rej_off，bflow_sign_off）

1. 根据form_id和状态为送签中在flow_maintain_isofiles表中查找，如果没有找到，返回（此單據編碼" + form_id + "不存在或者單據狀態不正確,操作失敗）

2. 根据form_id, user_id在flow_maintain_isofiles，bflow_sign_off两个表中查找判断抽單操作是否為申請人或操作人

   ```
   "select *  from (select create_user_id employ_id from flow_system.flow_maintain_isofiles as isoFrom" +
                   " where isoFrom.form_id = '"+form_id+"'" +
                   "  union select user_id employ_id from flow_system.bflow_sign_off as signOff  where" +
                   " from_id = '"+form_id+"'  and state = '10') a" +
                   "  where a.employ_id   = '"+user_id+"' ";
   ```

3.  根据form_id查找是否有签核记录

4. 如果有签核记录，將簽核信息插入bflow_rej_off表

5.  更新基本表,簽核檔(更新大表flow_maintain_isofiles，删除flow_sign_off表中对应信息，更新sign_off表)   



### ==ISO驳回==

流程：

关联表（flow_maintain_isofiles，bflow_rej_off，bflow_sign_off，flow_base_email，bak_user）

1. 根据form_id和状态为送签中在flow_maintain_isofiles表中查找，如果没有找到，返回（此單據編碼" + form_id + "不存在或者單據狀態不正確,操作失敗）
2. 根据form_id和flag=N在bflow_sign_off表中查找数据，如果没有找到，返回（此單據編碼" + form_id + "不存在或者單據狀態不正確,操作失敗）
3. 如果步骤2中找到数据，比对登陆user_id与bflow_sign_off表中的user_id是否一致，不一致返回（您的工號為" + user_id + "此單據" + form_id + "流程操作人應為" + sign_off1.getUser_id() + ",操作失敗!）
4. 根据form_id以及步骤2中查到的数据在bflow_sign_off 查询所有比他状态（ state）小的
5. 将步骤4找到的数据插入到bflow_rej_off，如果count等于0 ，返回（驳回失敗,請聯繫資訊！！）
6. 根据form_id，state=10,rej09=N,rej10=N,查询bflow_rej_off 数据，如果没有数据，返回（駁回操作失敗,請聯繫資訊！！）
7. 根据6中查找到的user_id 以及flag="Y" 查找有無代理人(包含代理人的email)    ==没有代理人=={1，将驳回信息插入到bflow_rej_off中，2，更新簽核檔bflow_sign_off，3，刪除簽核記錄bflow_sign_off，4，更新表單狀態 flow_maintain_isofiles }             ==有代理人===={1，拿到代理人信息，2，更新簽核檔,此处与无代理人有区别（插入信息为代理人信息）3，刪除簽核記錄  4，更新表單狀態}
8. 根据form_id，rej09=N,rej10=N,查询bflow_rej_off 、bak_user数据，包含了user的email
9. 设置邮件信息（如果没有代理人，将8中的user.email设置进邮件表flow_base_email）（如果有代理人，将7中的代理人.email设置进邮件表flow_base_email），设置邮件主题，邮件信息，
10. 发送邮件

### ==ISO会签==

#### 1，根据表单号获取流程信息

 关联表（flow_base_user，flow_base_signprocess）

- 获取user信息
- 获取各关信息



####  2，设定签核流程信息

 关联表（flow_base_signprocess）

- 根据form_id查询flow_base_signprocess 表信息，如果存在就删除，
- 将各关信息通过save方法保存在flow_base_signprocess 表

#### 3，根据表单号删除流程信息

- 通过表单号删除flow_base_signprocess  信息

#### 4，获取签核进度

