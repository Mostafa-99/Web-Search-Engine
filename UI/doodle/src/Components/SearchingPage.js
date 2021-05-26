import { useState, useEffect } from "react";
import { Route, BrowserRouter as Router } from "react-router-dom";
import logo from '../hadras.png'
import './SearchingPage.css'
import Posts from './Posts'
import Pagination from './Pagination'
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
function SearchingPage() {
    const [posts, setPosts] = useState([]);
    const [postsLength, setPostsLength] = useState();
    const [loading, setLoading] = useState(false);
    const [currentPage, setCurrentPage] = useState(localStorage.getItem('currentPage'));
    const [postsPerPage] = useState(10);
    const [searchText,setSearchText] = useState(localStorage.getItem('text'));

    useEffect(() => {
        const fetchPosts = async () => {
          setLoading(true);
          const res = await axios.get('https://jsonplaceholder.typicode.com/posts');


        //   setPosts(res.data);
            setPostsLength(dumm.length);
            if (currentPage == 1) {
                setPosts(dumm1)
            }
            else if(currentPage == 2) {
                setPosts(dumm2)
            }
            else if(currentPage == 3) {
                setPosts(dumm3)
            }
            else if(currentPage == 4) {
                setPosts(dumm4)
            }


          setLoading(false);
        };
    
        fetchPosts();
      }, []);

    const indexOfLastPost = currentPage * postsPerPage;
    const indexOfFirstPost = indexOfLastPost - postsPerPage;
    // const currentPosts = posts.slice(indexOfFirstPost, indexOfLastPost);
    const currentPosts = posts;

    // Change page
    const paginate = pageNumber => localStorage.setItem('currentPage',pageNumber);
    
    function mySubmitHandler(){
        localStorage.setItem('text', searchText);
        localStorage.setItem('currentPage', 1);
    }
  return (
    <div className="searching-page">
        {/* <a href="https://fontmeme.com/google-font/"><img src="https://fontmeme.com/permalink/210524/5a66607532bca256c144bd8a3cd07d16.png" alt="google-font" border="0"></a> */}
        <div className="searching-hadras-logo">
            <a href="/doodle"><img style={{height:'200px'}} src={logo} alt="hadras" border="0"/></a>
        </div>
        <div className="searching-logo">
            <a href="/doodle"><img src="https://fontmeme.com/permalink/210524/c504e219e1b6c0a480ba44921ad51c52.png" alt="google-font" border="0"/></a>
        </div>
        <div className="searching-textbox">
            <form action="/doodling" onSubmit={event=>mySubmitHandler()}>
                <input
                    autoFocus
                    placeholder = "   Enter search text..."
                    value = {searchText} 
                    type = 'text'
                    onChange = {event=>setSearchText(event.target.value)}
                    className = "searching-input-text"
                />
            </form>
        </div>
        <div className='container mt-5'>
            <h1 className='text-primary mb-3'>Results</h1>
            <Posts posts={currentPosts} loading={loading} />
            <Pagination
                postsPerPage={postsPerPage}
                totalPosts={postsLength}
                paginate={paginate}
            />
        </div>
    </div>
  );
}

export default SearchingPage;





 