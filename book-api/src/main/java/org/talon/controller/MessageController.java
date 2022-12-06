package org.talon.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.valves.JsonErrorReportValve;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.talon.mongo.Message;
import org.talon.rest.JsonResponse;
import org.talon.service.IMsgService;

import java.util.List;

/**
 * Created by Zelong
 * On 2022/10/28
 **/

@Slf4j
@RestController
@RequestMapping("msg")
public class MessageController {

    @Autowired
    private IMsgService msgService;

    @GetMapping("list")
    public JsonResponse list(@RequestParam String userId,
                                     @RequestParam Integer page,
                                     @RequestParam Integer pageSize){
        // mongodb start page index from 0
        // different from usual databases
        if (page == null) {
            page = 0;
        }

        if (pageSize == null){
            pageSize = 10;
        }

        List<Message> result = msgService.queryComments(userId, page, pageSize);

        return JsonResponse.ok(result);
    }
}
