package com.example.archunitfixtures.nocrossmoduleentityimports.violating.moduletwo.internal.domain;

import com.example.archunitfixtures.nocrossmoduleentityimports.violating.moduleone.internal.domain.Widget;

public class GadgetService {

	// BAD: 'moduletwo' code depends directly on 'moduleone''s @Entity.
	private Widget widget;
}
