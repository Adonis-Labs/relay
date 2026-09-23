package com.example.relay.catalog.internal.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/products/{productId}/skus", version = "v1+")
public class SkuController {
}
