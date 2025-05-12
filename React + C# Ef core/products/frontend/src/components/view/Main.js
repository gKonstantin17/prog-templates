import {NavBar} from "./NavBar";
import {MainContent} from "./MainContent";
import {useState} from "react";
import "./style.css";
function Main() {
    const [activeTab, setActiveTab] = useState('specification');
    return (
        <div className="main-container">
            <NavBar activeTab={activeTab} setActiveTab={setActiveTab} />
            <MainContent activeTab={activeTab} />
        </div>
    );
}
export {Main}
