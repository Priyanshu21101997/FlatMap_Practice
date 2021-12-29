package com.example.flatmappractice.network

import com.example.flatmappractice.Model.Comment
import com.example.flatmappractice.Model.Post
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface APIService {


    @GET("posts")
    fun getPosts(): Observable<List<Post>>

    @GET("posts/{id}/comments")
    fun getComments(@Path("id") postId:Int):Observable<List<Comment>>
}