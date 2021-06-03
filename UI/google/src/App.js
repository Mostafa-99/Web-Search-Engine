import './App.css';
import { Route, BrowserRouter as Router, Switch } from "react-router-dom";
import HomePage from './Components/HomePage'
import SearchingPage from './Components/SearchingPage'

function App() {
  return (
    <div className="App mt-5 pt-5"  style={{height:"100%"}}>
      <Switch>
        <Route path="/" component={HomePage} exact/>
        <Route path="/googling" component={SearchingPage} exact/>
      </Switch>
    </div>
  );
}

export default App;
