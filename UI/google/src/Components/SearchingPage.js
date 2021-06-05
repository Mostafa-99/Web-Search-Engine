import { useState, useEffect } from "react";
import { Route, BrowserRouter as Router } from "react-router-dom";
import logo from '../hadras.png'
import './SearchingPage.css'
import Posts from './Posts'
import Pagination from './Pagination'
import SpeechRec from "./SpeechRec"
import axios from 'axios';
const dumm1 = [
    {
        'name' : 'result 1',
        'id' : 1
    },
    {
        'name' : 'result 2',
        'id' : 2
    },
    {
        'name' : 'result 3',
        'id' : 3
    },
    {
        'name' : 'result 4',
        'id' : 4
    },
    {
        'name' : 'result 5',
        'id' : 5
    },
    {
        'name' : 'result 6',
        'id' : 6
    },
    {
        'name' : 'result 7',
        'id' : 7
    },
    {
        'name' : 'result 8',
        'id' : 8
    },
    {
        'name' : 'result 9',
        'id' : 9
    },
    {
        'name' : 'result 10',
        'id' : 10
    }
]
const dumm2 = [
    {
        'name' : 'result 11',
        'id' : 11
    },
    {
        'name' : 'result 12',
        'id' : 12
    },
    {
        'name' : 'result 13',
        'id' : 13
    },
    {
        'name' : 'result 14',
        'id' : 14
    },
    {
        'name' : 'result 15',
        'id' : 15
    },
    {
        'name' : 'result 16',
        'id' : 16
    },
    {
        'name' : 'result 17',
        'id' : 17
    },
    {
        'name' : 'result 18',
        'id' : 18
    },
    {
        'name' : 'result 19',
        'id' : 19
    },
    {
        'name' : 'result 20',
        'id' : 20
    }
]
const dumm3 = [
    {
        'name' : 'result 21',
        'id' : 21
    },
    {
        'name' : 'result 22',
        'id' : 22
    },
    {
        'name' : 'result 23',
        'id' : 23
    },
    {
        'name' : 'result 24',
        'id' : 24
    },
    {
        'name' : 'result 25',
        'id' : 25
    },
    {
        'name' : 'result 26',
        'id' : 26
    },
    {
        'name' : 'result 27',
        'id' : 27
    },
    {
        'name' : 'result 28',
        'id' : 28
    },
    {
        'name' : 'result 29',
        'id' : 29
    },
    {
        'name' : 'result 30',
        'id' : 30
    }
]
const dumm4 = [
    {
        'name' : 'result 31',
        'id' : 31
    },
    {
        'name' : 'result 32',
        'id' : 32
    },
    {
        'name' : 'result 33',
        'id' : 33
    },
    {
        'name' : 'result 34',
        'id' : 34
    },
    {
        'name' : 'result 35',
        'id' : 35
    }
]
const dumm = [
    {
        'name' : 'result 1',
        'id' : 1
    },
    {
        'name' : 'result 2',
        'id' : 2
    },
    {
        'name' : 'result 3',
        'id' : 3
    },
    {
        'name' : 'result 4',
        'id' : 4
    },
    {
        'name' : 'result 5',
        'id' : 5
    },
    {
        'name' : 'result 6',
        'id' : 6
    },
    {
        'name' : 'result 7',
        'id' : 7
    },
    {
        'name' : 'result 8',
        'id' : 8
    },
    {
        'name' : 'result 9',
        'id' : 9
    },
    {
        'name' : 'result 10',
        'id' : 10
    },
    {
        'name' : 'result 11',
        'id' : 11
    },
    {
        'name' : 'result 12',
        'id' : 12
    },
    {
        'name' : 'result 13',
        'id' : 13
    },
    {
        'name' : 'result 14',
        'id' : 14
    },
    {
        'name' : 'result 15',
        'id' : 15
    },
    {
        'name' : 'result 16',
        'id' : 16
    },
    {
        'name' : 'result 17',
        'id' : 17
    },
    {
        'name' : 'result 18',
        'id' : 18
    },
    {
        'name' : 'result 19',
        'id' : 19
    },
    {
        'name' : 'result 20',
        'id' : 20
    },
    {
        'name' : 'result 21',
        'id' : 21
    },
    {
        'name' : 'result 22',
        'id' : 22
    },
    {
        'name' : 'result 23',
        'id' : 23
    },
    {
        'name' : 'result 24',
        'id' : 24
    },
    {
        'name' : 'result 25',
        'id' : 25
    },
    {
        'name' : 'result 26',
        'id' : 26
    },
    {
        'name' : 'result 27',
        'id' : 27
    },
    {
        'name' : 'result 28',
        'id' : 28
    },
    {
        'name' : 'result 29',
        'id' : 29
    },
    {
        'name' : 'result 30',
        'id' : 30
    },
    {
        'name' : 'result 31',
        'id' : 31
    },
    {
        'name' : 'result 32',
        'id' : 32
    },
    {
        'name' : 'result 33',
        'id' : 33
    },
    {
        'name' : 'result 34',
        'id' : 34
    },
    {
        'name' : 'result 35',
        'id' : 35
    }
]
const dumm_names = [
    {
        "name" : "ahmed"
    },
    {
        "name" : "ahmar"
    },
    {
        "name" : "ahmad"
    },
    {
        "name" : "ahmadany"
    },
    {
        "name" : "ahmadan"
    }
]
function SearchingPage() {
    const [posts, setPosts] = useState([]);
    const [postsLength, setPostsLength] = useState();
    const [loading, setLoading] = useState(false);
    const [currentPage, setCurrentPage] = useState(localStorage.getItem('currentPage'));
    const [postsPerPage] = useState(10);
    const [searchText,setSearchText] = useState(localStorage.getItem('text'));
    const [currentText,setCurrentText] = useState(localStorage.getItem('text'));
    const [dataListName,setDataListName] = useState([]);
    // const [searchText,setSearchText] = useState("");
    
    useEffect(() => {
        if(currentText!==""){
            axios.get("http://localhost:9090/Search/Suggestions/"+currentText)    
            .then(res => {
              if(res.status===200)
              {
                //   console.log("length req: "+res.data.Count)
                    setDataListName(res.data);
                    // setPostsLength(res.data.Count);
              }
              else
              {
                // alert("Something went wrong please refresh the page!")
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
          if(searchText!==""){
              axios.get("http://localhost:9090/Length/"+searchText)    
              .then(res => {
                if(res.status===200)
                {
                  //   console.log("length req: "+res.data.Count)
                      setPostsLength(res.data.Count);
                }
                else
                {
                  // alert("Something went wrong please refresh the page!")
                }   
              }).catch(err=>{
                  alert(err)
              })
          }
        }, [searchText]);

    useEffect(() => {
        const fetchPosts = async () => {
          setLoading(true);

            console.log("searching...")

            if(searchText!==""){
                axios.get("http://localhost:9090/Search/"+searchText+"?pageNo="+(currentPage-1))   
                .then(res => {
                  if(res.status===200)
                  {
                    console.log("res inside length: "+res.data.length);
                    console.log("currpage: "+currentPage);
                    console.log(res.data)
                    setPosts(res.data);
                    // setPostsLength(res.data.length);
                  }
                  else
                  {
                    // alert("Something went wrong please refresh the page!")
                  }   
                }).catch(err=>{
                    alert(err)
                })    
            }
          setLoading(false);
        };
        
        fetchPosts();
      }, [searchText,currentPage]);

    const indexOfLastPost = currentPage * postsPerPage;
    const indexOfFirstPost = indexOfLastPost - postsPerPage;
    const currentPosts = posts;

    // Change page
    function paginate (pageNumber){
        localStorage.setItem('currentPage',pageNumber);
        // fetchPosts()
    }
    function mySubmitHandler(){
        localStorage.setItem('text', currentText);
        localStorage.setItem('currentPage', 1);
        setSearchText(currentText);
        setCurrentPage(1);
    }
    function speechRecHandler(t){
        localStorage.setItem('text', t);
        localStorage.setItem('currentPage', 1);
        setCurrentText(t)
        setSearchText(t);
        setCurrentPage(1);
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
                            <option value={post.name}/>
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
            currentPosts.length === 0?
            ""
            :
            <div className='container mt-5'>
                {/* <h1 className='text-primary mb-3'>Results</h1> */}
                <Posts posts={currentPosts} loading={loading} />
                <Pagination
                    postsPerPage={postsPerPage}
                    totalPosts={postsLength}
                    paginate={paginate}
                />
            </div>
        }
    </div>
  );
}

export default SearchingPage;





 