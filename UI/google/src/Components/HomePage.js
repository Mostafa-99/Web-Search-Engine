import { useState } from "react";
import { Route, BrowserRouter as Router } from "react-router-dom";
import './HomePage.css'
function HomePage() {
    const [text,setText] = useState('');
    function mySubmitHandler(){
        localStorage.setItem('text', text);
        localStorage.setItem('currentPage', 1);
    }

  return (
    <div className="home-page mt-5 pt-5">
        <div className="home-logo mt-5 pt-5">
            <a  href="/"><img src="https://fontmeme.com/permalink/210603/602a6c2cb5391fdb7ec767ef92e84e3e.png" alt="google-font" border="0"/></a>
        </div>
        <div className="home-textbox">
            <form action="/googling" onSubmit={event=>mySubmitHandler()}>
                <input
                    style={{paddingLeft:"20px"}}
                    autoFocus
                    placeholder = "Enter search text..."
                    type='text'
                    onChange={event=>setText(event.target.value)}
                    className="home-input-text"
                />
            </form>
        </div>
    </div>
  );
}

export default HomePage;
