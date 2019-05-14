package com.theoxao.book.controller

import com.theoxao.book.dto.BookDTO
import com.theoxao.book.dto.BookDetailDTO
import com.theoxao.book.service.BookService
import com.theoxao.commons.web.RestResponse
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("book")
class BookController(private val bookService: BookService) {


    @ApiOperation("根据书籍ISBN")
    @GetMapping("/isbn/{isbn}")
    fun findByISBN(@PathVariable("isbn") isbn: String): Mono<RestResponse<List<BookDTO>>> {
        return bookService.findByIsbn(isbn)
    }

    @ApiOperation("根据书籍内部编号查询书籍")
    @GetMapping("/find_by_id")
    fun findById(@RequestParam id: String): RestResponse<BookDetailDTO> {
        return bookService.findById(id)
    }

    @RequestMapping("/search")
    fun search(kw: String ,type :String? ): Mono<RestResponse<List<BookDTO>>> {
        return bookService.search(kw ,type)
    }


    @RequestMapping("/sync")
    fun sync(pwd: String) {
        bookService.sync()
    }

    @RequestMapping("/sync_es")
    fun syncEs(pwd: String, drop: Int?) {
        assert(pwd == "mk")
        bookService.syncES(drop)
    }

    @RequestMapping("/updatePage")
    fun updatePage(){
        bookService.updatePage()
    }

}
