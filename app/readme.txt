
本文档说明使用接口时应注意的一些事项。

开发者平台HOST: http://develop.runwise.cn

目前只支持接入该HOST，ERP平台暂不维护。开通时间待通知！
目前开发者平台服务器处于多数据库模式，接口接入不同的数据库需要设置请求的Headers。

Key: X-Odoo-Db Value: 数据库名
部分接口需要用户权限才能正常使用，在文档中根据：AUTH 来区分。

AUTH: none 不需要用户权限 user 需要用户权限
在接口反馈信息中通常以State来区分请求成功与否。

pickingState: "A0006" 请求成功， "A0001" 请求失败
在接口请求时，可能会出现一些无法截获的错误。因为Odoo本身将这些错误截获并回馈了。

例如：此类错误目前无法进行截获

{
    "jsonrpc": "2.0",
    "id": null,
    "error": {
    "message": "Odoo Session Expired",
    "code": 100,
    "data": {
        "debug": "Traceback (most recent call last):\n  File \"/usr/local/lib/python2.7/dist-packages/odoo-10.0.post20161021-py2.7.egg/odoo/http.py\", line 638, in _handle_exception\n    return super(JsonRequest, self)._handle_exception(exception)\n  File \"/root/runwise/odoo/addons/base/ir/ir_http.py\", line 184, in _dispatch\n    auth_method = cls._authenticate(func.routing[\"auth\"])\n  File \"/root/runwise/odoo/addons/base/ir/ir_http.py\", line 112, in _authenticate\n    getattr(cls, \"_auth_method_%s\" % auth_method)()\n  File \"/root/runwise/odoo/addons/base/ir/ir_http.py\", line 85, in _auth_method_user\n    raise http.SessionExpiredException(\"Session expired\")\nSessionExpiredException: Session expired\n",
        "exception_type": "internal_error",
        "message": "Session expired",
        "name": "odoo.http.SessionExpiredException",
        "arguments": [
            "Session expired"
            ]
        }
    }
}