import {LeftBar} from "../LeftBar";
import {SpecContent} from "./Content/SpecContent";
import {useState} from "react";

function Specification() {
    const [activeTab, setActiveTab] = useState('all');
    const tabs = [
        {id:"all",name:"Вся спецификация"},
        {id:"count",name:"Посчитать компоненты"},
        {id:"add",name:"Добавить"},
        {id:"change",name:"Изменить"},
        {id:"delete",name:"Удалить"}
    ]

    return (<div className="content">
        <LeftBar tabs={tabs} activeTab={activeTab} setActiveTab={setActiveTab}/>
        <SpecContent activeTab={activeTab}/>
    </div>)
}
export {Specification}