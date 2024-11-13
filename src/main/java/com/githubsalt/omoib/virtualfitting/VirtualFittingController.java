package com.githubsalt.omoib.virtualfitting;

import com.githubsalt.omoib.global.config.security.JwtProvider;
import com.githubsalt.omoib.virtualfitting.dto.FittingRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/virtual-fitting")
@RestController
@Slf4j
public class VirtualFittingController {

    private final VirtualFittingService virtualFittingService;
    private final JwtProvider jwtProvider;

    @PostMapping("/fitting")
    public ResponseEntity<String> fitting(
            HttpServletRequest request,
            @RequestBody FittingRequestDTO requestDTO) {

        log.info("가상 피팅 요청: {}", requestDTO);

        Long userId = jwtProvider.getUserId(request);
        return virtualFittingService.fitting(userId, requestDTO);

//        return ResponseEntity.accepted().build();
    }
}
