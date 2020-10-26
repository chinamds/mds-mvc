/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.web.jcaptcha;

import com.octo.captcha.Captcha;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-3-22 下午3:38
 * <p>Version: 1.0
 */
public class EsManageableImageCaptchaService extends DefaultManageableImageCaptchaService {

    public EsManageableImageCaptchaService(com.octo.captcha.service.captchastore.CaptchaStore captchaStore, com.octo.captcha.engine.CaptchaEngine captchaEngine, int minGuarantedStorageDelayInSeconds, int maxCaptchaStoreSize, int captchaStoreLoadBeforeGarbageCollection) {
        super(captchaStore, captchaEngine, minGuarantedStorageDelayInSeconds, maxCaptchaStoreSize, captchaStoreLoadBeforeGarbageCollection);
    }

    public boolean hasCapcha(String id, String userCaptchaResponse) {
    	Captcha captcha= store.getCaptcha(id);
    	if (captcha != null)
    		return captcha.validateResponse(userCaptchaResponse);
    	
    	return false;
    }
}
