import {LeftBar} from "../LeftBar";
import {OrderContent} from "./Content/OrderContent";
import {useState} from "react";

function Order() {
    const [activeTab, setActiveTab] = useState('all');
    const tabs = [
        {id:"all",name:"Все заказы"},
        {id:"add",name:"Добавить"},
        {id:"change",name:"Изменить"},
        {id:"delete",name:"Удалить"}
    ]

    return (<div className="content">
        <LeftBar tabs={tabs} activeTab={activeTab} setActiveTab={setActiveTab}/>
        <OrderContent  activeTab={activeTab}/>
    </div>)
}
export {Order}