package com.kids.commonframe.base;

import com.alibaba.fastjson.JSONObject;

import org.json.JSONArray;

import java.util.List;

/**
 * 没一个实体类都要继承该类
 */
public class BaseEntity {
    /**
     * jsonrpc : 2.0
     * id : null
     * result : {"state":"A0006","data":{"isSuccess":false,"mobile":"13737574563"}}
     */

    private String jsonrpc;
    private Object id;
    private ResultBean result;
    /**
     * error : {"message":"Odoo Server Error","code":200,"data":{"debug":"Traceback (most recent call last):\n File \"/usr/local/lib/python2.7/dist-packages/odoo-10.0.post20161021-py2.7.egg/odoo/http.py\", line 638, in _handle_exception\n return super(JsonRequest, self)._handle_exception(exception)\n File \"/usr/local/lib/python2.7/dist-packages/odoo-10.0.post20161021-py2.7.egg/odoo/http.py\", line 1447, in _dispatch_nodb\n func, arguments = self.nodb_routing_map.bind_to_environ(request.httprequest.environ).match()\n File \"/usr/local/lib/python2.7/dist-packages/werkzeug/routing.py\", line 1430, in match\n raise NotFound()\nNotFound: 404: Not Found\n","exception_type":"internal_error","message":"","name":"werkzeug.exceptions.NotFound","arguments":[]}}
     */

    private ErrorBean error;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public ErrorBean getError() {
        return error;
    }

    public void setError(ErrorBean error) {
        this.error = error;
    }

    public static class ErrorBean {
        /**
         * message : Odoo Server Error
         * code : 200
         * data : {"debug":"Traceback (most recent call last):\n File \"/usr/local/lib/python2.7/dist-packages/odoo-10.0.post20161021-py2.7.egg/odoo/http.py\", line 638, in _handle_exception\n return super(JsonRequest, self)._handle_exception(exception)\n File \"/usr/local/lib/python2.7/dist-packages/odoo-10.0.post20161021-py2.7.egg/odoo/http.py\", line 1447, in _dispatch_nodb\n func, arguments = self.nodb_routing_map.bind_to_environ(request.httprequest.environ).match()\n File \"/usr/local/lib/python2.7/dist-packages/werkzeug/routing.py\", line 1430, in match\n raise NotFound()\nNotFound: 404: Not Found\n","exception_type":"internal_error","message":"","name":"werkzeug.exceptions.NotFound","arguments":[]}
         */

        private String message;
        private int code;
        private DataBean data;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * debug : Traceback (most recent call last):
             * File "/usr/local/lib/python2.7/dist-packages/odoo-10.0.post20161021-py2.7.egg/odoo/http.py", line 638, in _handle_exception
             * return super(JsonRequest, self)._handle_exception(exception)
             * File "/usr/local/lib/python2.7/dist-packages/odoo-10.0.post20161021-py2.7.egg/odoo/http.py", line 1447, in _dispatch_nodb
             * func, arguments = self.nodb_routing_map.bind_to_environ(request.httprequest.environ).match()
             * File "/usr/local/lib/python2.7/dist-packages/werkzeug/routing.py", line 1430, in match
             * raise NotFound()
             * NotFound: 404: Not Found
             * <p>
             * exception_type : internal_error
             * message :
             * name : werkzeug.exceptions.NotFound
             * arguments : []
             */

            private String debug;
            private String exception_type;
            private String message;
            private String name;
            private List<?> arguments;

            public String getDebug() {
                return debug;
            }

            public void setDebug(String debug) {
                this.debug = debug;
            }

            public String getException_type() {
                return exception_type;
            }

            public void setException_type(String exception_type) {
                this.exception_type = exception_type;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<?> getArguments() {
                return arguments;
            }

            public void setArguments(List<?> arguments) {
                this.arguments = arguments;
            }
        }
    }

    public static class ResultBean {
        /**
         * state : A0006
         * data : {"isSuccess":false,"mobile":"13737574563"}
         */

        private String state;
        private String error;
        private Object data;
        private JSONObject dataJson;
        private Object orders;

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public JSONObject getDataJson() {
            return dataJson;
        }

        public void setDataJson(JSONObject dataJson) {
            this.dataJson = dataJson;
        }

        public Object getOrders() {
            return orders;
        }

        public void setOrders(Object orders) {
            this.orders = orders;
        }
    }

    //	private String err_code;//": 0,
    private String msg;//": "success"

//	public void setData(Object object) {
//		this.getResult().setData(object);
//	}
//
//	public Object getData() {
//		return this.getResult().getData();
//	}


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
