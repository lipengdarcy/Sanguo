(function( factory ) {
	if ( typeof define === "function" && define.amd ) {
		define( ["jquery", "../jquery.validate"], factory );
	} else {
		factory( jQuery );
	}
}(function( $ ) {

/*
 * Translated default messages for the jQuery validation plugin.
 * Locale: ZH (Chinese, 中文 (Zhōngwén), 汉语, 漢語)
 */
$.extend($.validator.messages, {
	required: "<div class='tisyz'><i class='tisbefore'></i>这是必填字段</div>",
	remote: "<div class='tisyz'><i class='tisbefore'></i>请修正此字段</div>",
	email: "<div class='tisyz'><i class='tisbefore'></i>请输入有效的电子邮件地址</div>",
	url: "<div class='tisyz'><i class='tisbefore'></i>请输入有效的网址</div>",
	date: "<div class='tisyz'><i class='tisbefore'></i>请输入有效的日期</div>",
	dateISO: "<div class='tisyz'><i class='tisbefore'></i>请输入有效的日期 (YYYY-MM-DD)</div>",
	number: "<div class='tisyz'><i class='tisbefore'></i>请输入有效的数字</div>",
	digits: "<div class='tisyz'><i class='tisbefore'></i>只能输入数字</div>",
	creditcard: "<div class='tisyz'><i class='tisbefore'></i>请输入有效的信用卡号码</div>",
	equalTo: "<div class='tisyz'><i class='tisbefore'></i>你两次输入的值不相同</div>",
	extension: "<div class='tisyz'><i class='tisbefore'></i>请输入有效的后缀</div>",
	maxlength: $.validator.format("<div class='tisyz'><i class='tisbefore'></i>最多可以输入 {0} 个字符</div>"),
	minlength: $.validator.format("<div class='tisyz'><i class='tisbefore'></i>最少要输入 {0} 个字符</div>"),
	rangelength: $.validator.format("<div class='tisyz'><i class='tisbefore'></i>请输入长度在 {0} 到 {1} 之间的字符串</div>"),
	range: $.validator.format("<div class='tisyz'><i class='tisbefore'></i>请输入范围在 {0} 到 {1} 之间的数值</div>"),
	max: $.validator.format("<div class='tisyz'><i class='tisbefore'></i>请输入不大于 {0} 的数值</div>"),
	min: $.validator.format("<div class='tisyz'><i class='tisbefore'></i>请输入不小于 {0} 的数值</div>")
});

}));