package com.githubsalt.omoib.aws.lambda;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/lambda")
@RequiredArgsConstructor
public class LambdaController {

    private final LambdaService lambdaService;

    // Lambda 콜백 처리 API
    @PostMapping("/callback")
    public void handleLambdaCallback(@RequestBody String result) {
        // Lambda 결과 처리 로직

        // TODO 콜백 처리 시 결과 필드의 인덱스나 네임 필드를 읽고, 그에 해당하는 서비스로 결과를 전달하는 방식으로 구현할듯
        log.info("Received Lambda result: {}", result);
    }

    // Lambda 호출 테스트용 API
    @PostMapping("/invoke")
    public void invoke(@RequestParam(name = "functionName") String functionName, @RequestBody(required = false) String requestBody) {
        log.info("Lambda call test with RequestBody : {}", requestBody);
        lambdaService.invokeLambdaAsync(functionName, requestBody);
    }
}
