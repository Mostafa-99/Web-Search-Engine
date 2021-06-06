import { useState, useEffect } from "react";
import { Route, BrowserRouter as Router } from "react-router-dom";
import logo from '../hadras.png'
import './SearchingPage.css'
import Posts from './Posts'
import Pagination from './Pagination'
import SpeechRec from "./SpeechRec"
import axios from 'axios';

function SearchingPage() {
    const [posts, setPosts] = useState([]);
    const [postsLength, setPostsLength] = useState();
    const [loading, setLoading] = useState(true);
    const [currentPage, setCurrentPage] = useState(localStorage.getItem('currentPage'));
    const [postsPerPage] = useState(10);
    const [searchText,setSearchText] = useState(localStorage.getItem('text'));
    const [currentText,setCurrentText] = useState(localStorage.getItem('text'));
    const [dataListName,setDataListName] = useState([]);
    
    useEffect(() => {
        if(currentText!==""){
            axios.get("http://localhost:9090/Search/Suggestions/"+currentText)    
            .then(res => {
              if(res.status===200)
              {
                    setDataListName(res.data);
              }
              else
              {
              }   
            }).catch(err=>{
                alert(err)
            })
        }
        else{
            setDataListName([])
        }
      }, [currentText]);

      useEffect(() => {
          if(searchText!=="" && searchText!==" " && searchText!=="  "){
              axios.get("http://localhost:9090/Length/"+searchText)    
              .then(res => {
                if(res.status===200)
                {
                      setPostsLength(res.data.Count);
                } 
              }).catch(err=>{
                  alert(err)
              })
          }
        }, [searchText]);

    useEffect(() => {
        const fetchPosts = async () => {
          setLoading(true);
            
            if(searchText!==""){
                axios.get("http://localhost:9090/Search/"+searchText+"?pageNo="+(currentPage-1))   
                .then(res => {
                  if(res.status===200)
                  {
                    setPosts(res.data);
                    setLoading(false);
                  }  
                }).catch(err=>{
                    alert(err)
                })    
            }
        };
        
        fetchPosts();
      }, [searchText,currentPage]);

    const indexOfLastPost = currentPage * postsPerPage;
    const indexOfFirstPost = indexOfLastPost - postsPerPage;
    const currentPosts = posts;

    // Change page
    function paginate (pageNumber){
        localStorage.setItem('currentPage',pageNumber);
    }
    function mySubmitHandler(){
        localStorage.setItem('text', currentText);
        localStorage.setItem('currentPage', 1);
        setSearchText(currentText);
        setCurrentPage(1);
    }
    function speechRecHandler(t){
        setCurrentText(t)
        setSearchText(t);
        setCurrentPage(1);
        localStorage.setItem('text', t);
        localStorage.setItem('currentPage', 1);
    }
  return (
    <div className="searching-page">
        <div className="searching-logo">
            <a href="/"><img src="https://fontmeme.com/permalink/210603/602a6c2cb5391fdb7ec767ef92e84e3e.png" alt="google-font" border="0"/></a>
        </div>
        <div className="searching-textbox">
            <form action="/googling" onSubmit={event=>mySubmitHandler()}>
                <input
                    style={{paddingLeft:"20px"}}
                    autoFocus
                    placeholder = "Enter search text..."
                    value = {currentText} 
                    type = 'text'
                    onChange = {event=>setCurrentText(event.target.value)}
                    className = "searching-input-text"
                    list="datalistOptions"
                    // form-control 
                />
                <datalist id="datalistOptions" style={{width:"100%"}}>
                    {
                        dataListName.length == 0?
                        ""
                        :
                        dataListName.map((post,num) => (
                            <option value={post.word}/>
                          ))
                    }
                </datalist>
            </form>
            <SpeechRec
                onClick={() => setCurrentPage(1)}
                speechRecHandler = {speechRecHandler}
            />
        </div>
        {
                loading == true? 
                <div class="spinner-border text-primary mt-5" role="status">
                <span class="sr-only"></span>
                </div>                :
                <div className='container mt-3'>
                    {
                        postsLength?
                        <h3 className='text-primary mb-4'>Your searched word has {postsLength} results</h3> 
                        :
                        <h3 className='text-primary mb-4'>Your searched word has no results</h3>
                    }
                    <Posts posts={currentPosts} loading={loading} />
                    <Pagination
                        postsPerPage={postsPerPage}
                        totalPosts={postsLength}
                        loading={loading}
                        paginate={paginate}
                    />
                </div>
        }
    </div>
  );
}

export default SearchingPage;





 