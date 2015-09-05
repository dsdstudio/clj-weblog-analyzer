(function(W, doc) {
	function uuid() {
		return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
		   	var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
		   	return v.toString(16);
		});
	}
	var CookieService = (function () {
		function getDomain() {
			var h = document.domain;
			var domainTokens = h.split('.');
			return domainTokens.length > 3 ? h : domainTokens.splice(1,2).join('.');
		}
		function addCookie(k, v) {
			var t0 = k + '=' + encodeURIComponent(v) + '; domain=' + getDomain() + '; path=/;';
			document.cookie = t0;
		}
		function persistCookie(k, v) {
			var expiredDate = new Date();
			expiredDate.setYear(expiredDate.getYear() + 100);
			var t0 = k + '=' + encodeURIComponent(v) + '; domain=' + getDomain() + '; path=/; expires=' + expiredDate.toGMTString();
			document.cookie = t0;
			return v;
		}
		function removeCookie(k, v) {
			document.cookie = k + '=' + encodeURIComponent(v) + '; domain=' + getDomain() + '; path=/; expires=' + new Date(0).toGMTString();
		}
		function getCookie(k) {
			var cookieFields = document.cookie.split('; ');
			var i = cookieFields.length;
			var cookies = {};
			while(i--) {
				var fieldTokens = cookieFields[i].split('=');
				cookies[fieldTokens[0]] = fieldTokens[1];
			}
			return cookies[k];
		}
		return {
			remove:removeCookie,
			put:addCookie,
			get:getCookie,
			persist:persistCookie
		};
	})();

	var loggingUrl, referer, ua, firstVisit, sid, img;
	W.BZ = W.BZ || {};
	if ( !W.BZ.id ) throw new Error('아이디를 설정해주세요');
	if ( W.BZ.debug ) {
		loggingUrl = 'http://localhost:3000/logging?';
	} else {
		loggingUrl = 'http://anl.pethostel.net:9999/logging?';
	}

	referer = document.location.referer || '';
	ua = navigator.userAgent,
	sid = CookieService.get('__bz_sid'),
	firstVisit = !sid;
	if ( firstVisit ) {
		sid = CookieService.persist('__bz_sid', uuid());
	}

	function createParam () {
		return [
			'bzid=' + BZ.id,
			'request=' + encodeURIComponent(location.href),
			'referer=' + encodeURIComponent(referer),
			'fv=' + (firstVisit ? 1 : 0),
			'sid=' + sid,
			'ua=' + encodeURIComponent(ua),
			'dt=' + (+new Date)
		].join('&');
	}
	function logging() {
		img = new Image();
		img.src = loggingUrl + createParam();
	}
	logging();
})(this, document);
