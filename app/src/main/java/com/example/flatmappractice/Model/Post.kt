package com.example.flatmappractice.Model

 data class Post(val userId:Int, val id:Int, val title:String, val body:String, var comments:List<Comment> ) {
}