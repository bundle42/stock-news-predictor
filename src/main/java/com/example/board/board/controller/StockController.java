package com.example.board.board.controller;

import com.example.board.board.service.PredictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class StockController {

    private final PredictService predictService;

    @GetMapping("/stock/predict")
    public String predictStock(@RequestParam("stockName") String stockName, Model model) {
        try {
            Map<String, Object> prediction = predictService.start(stockName);
            model.addAttribute("prediction", prediction);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }

        return "board/predict";
    }
}