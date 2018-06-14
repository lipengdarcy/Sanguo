// 身份证号码验证
			jQuery.validator.addMethod("isIdCardNo",
					function(value, element) {
						//var idCard = /^(\d{6})()?(\d{4})(\d{2})(\d{2})(\d{3})(\w)$/;   
						return this.optional(element)
								|| isIdCardNo(value,
										cardType);
					}, "<div class='tisyz'><i class='tisbefore'></i>身份证号长度不正确或不符合规定</div>");

			jQuery.validator.addMethod("isIdCardNo1",
					function(value, element) {
						//var idCard = /^(\d{6})()?(\d{4})(\d{2})(\d{2})(\d{3})(\w)$/;   
						return this.optional(element)
								|| isIdCardNo1(value,
										cardType);
					}, "<div class='tisyz'><i class='tisbefore'></i>出生日期格式不正确</div>");

			jQuery.validator.addMethod("isIdCardNo2",
					function(value, element) {
						//var idCard = /^(\d{6})()?(\d{4})(\d{2})(\d{2})(\d{3})(\w)$/;   
						return this.optional(element)
								|| isIdCardNo2(value,
										cardType);
					}, "<div class='tisyz'><i class='tisbefore'></i>地址编码错误</div>");

			jQuery.validator.addMethod("isIdCardNo3",
					function(value, element) {
						//var idCard = /^(\d{6})()?(\d{4})(\d{2})(\d{2})(\d{3})(\w)$/;   
						return this.optional(element)
								|| isIdCardNo3(value,
										cardType, sex);
					}, "<div class='tisyz'><i class='tisbefore'></i>性别与身份证号不一致</div>");

			jQuery.validator.addMethod("isIdCardNo4",
					function(value, element) {
						//var idCard = /^(\d{6})()?(\d{4})(\d{2})(\d{2})(\d{3})(\w)$/;   
						return this.optional(element)
								|| isIdCardNo4(value, cardType);
					}, "<div class='tisyz'><i class='tisbefore'></i>不允许输入中文字</div>");
			jQuery.validator.addMethod("isIdCardNo5",
					function(value, element) {
						//var idCard = /^(\d{6})()?(\d{4})(\d{2})(\d{2})(\d{3})(\w)$/;   
						return this.optional(element)
								|| isIdCardNo5(value, cardType, date);
					}, "<div class='tisyz'><i class='tisbefore'></i>出生日期与身份证不一致</div>");
			// 手机号码验证    
			jQuery.validator
					.addMethod(
							"isMobile",
							function(value, element) {
								var length = value.length;
								return this
										.optional(element)
										|| (length == 11 && /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/
												.test(value));
							}, "<div class='tisyz'><i class='tisbefore'></i>请正确填写您的手机号码</div>");

			// 电话号码验证    
			jQuery.validator.addMethod("isPhone", function(
					value, element) {
				var tel = /^(\d{3,4}-?)?\d{7,9}$/g;
				return this.optional(element)
						|| (tel.test(value));
			}, "<div class='tisyz'><i class='tisbefore'></i>请正确填写您的电话号码</div>");
			
			// 邮箱验证    
			jQuery.validator.addMethod("isemail", function(
					value, element) {
				var tel = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((.[a-zA-Z0-9_-]{2,3}){1,2})$/;
				return this.optional(element)
						|| (tel.test(value));
			}, "<div class='tisyz'><i class='tisbefore'></i>邮箱格式错误</div>");
			
			//证件类型下拉框变换时即时赋值
			$("#certificateType").change(function() {
				cardType = $("#certificateType").val();

			});

			//性别按钮变换时即时赋值
			$("input:radio[name='basic.gender']")
					.change(
							function() {
								sex = $(
										"input[name='basic.gender']:checked")
										.val();
							});
			//出生日期变化时即时赋值
			$("#birthSelector").change(function() {
				date = $("#birthSelector").val();
		
					
			});
