package com.ef.wss.newscollect.service.impl;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.ef.wss.newscollect.generalcommon.Extract;
import com.ef.wss.newscollect.generalcommon.WebDocument;
import com.ef.wss.newscollect.pojo.PageInfo;
import com.ef.wss.newscollect.service.GeneralService;

@Service
public class GeneralServiceImpl implements GeneralService {
	@Override
    public WebDocument getWebDocumentByHtml(String html) {
        WebDocument document  = null;
        PageInfo info = new PageInfo();
        document = Extract.extractByHTML(html);
        info.setArticle(document.getContent());
        info.setCommitTime((String.valueOf(new Date().getTime())));
        info.setSource(html);
        
        return document;
    }
}
