<script setup lang="ts">
import {ref} from "vue";
import axios from "axios";

//const count = ref(0)

const title = ref("")
const content = ref("")

const write = function() {
    //alert(title + "/" + content);

    console.log(title.value, content.value);

    //axios.get("http://localhost:8080/api/hello");
    axios.post("http://localhost:8080/hodol/posts", {
        title: title.value,
        content: content.value
    });

}
</script>

<template>
    <!--<p>안녕하세요.</p> <button @click="count+=1">버튼을 눌러주세요</button>
    <p>{{count}}</p>-->

    <div class="">
        <div>
            <el-input v-model="title" type="text" placeholder="제목을 입력해 주세요."/>
        </div>
        <div class="mt-2">
            <el-input v-model="content" type="textarea" rows="15"></el-input>
        </div>

        <div class="mt-2">
            <el-button type="primary" @click="write()">글작성</el-button>
        </div>
    </div>


</template>

<style>
</style>