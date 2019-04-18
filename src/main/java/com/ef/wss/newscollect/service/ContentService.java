package com.ef.wss.newscollect.service;

import org.springframework.stereotype.Service;

import com.ef.wss.newscollect.generalcommon.WebDocument;
import com.ef.wss.newscollect.pojo.PageInfo;

@Service
public interface ContentService {

	void insertContent(PageInfo pageInfo);

	PageInfo queryContentsByUrl(String url);

	WebDocument getContentWebDocument(String url);
}
