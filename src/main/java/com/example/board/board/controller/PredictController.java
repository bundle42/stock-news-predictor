package com.example.board.board.controller;

import com.example.board.board.service.PredictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PredictController {

    private final PredictService predictService;

    @GetMapping("/api/predict")
    @ResponseBody
    public Map<String, Object> predictStockApi(@RequestParam("stockName") String stockName) {
        return predictService.start(stockName);
    }

    @PostMapping("/api/train")
    @ResponseBody
    public Map<String, Object> trainStockApi(@RequestParam("stockName") String stockName) {
        return predictService.train(stockName);
    }
}