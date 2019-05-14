package com.theoxao.read.controller

import com.theoxao.commons.web.RestResponse
import com.theoxao.read.model.Music
import com.theoxao.read.service.MusicService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
@RequestMapping("music")
class MusicController(private val musicService: MusicService) {

    @GetMapping("list")
    fun musicList(): Mono<RestResponse<List<Music>>> {
        return musicService.musicList()
    }

}