import {LeftBar} from "../LeftBar";
import {OrderContent} from "../order/Content/OrderContent";
import {useState} from "react";
import {SpecContent} from "../spec/Content/SpecContent";
import {WarehouseContent} from "./Content/WarehouseContent";

function Warehouse() {
    const [activeTab, setActiveTab] = useState('all');
    const tabs = [
        {id:"all",name:"Весь товар"},
        {id:"check",name: "Проверить наличие"},
        {id:"change",name: "Изменить"},
        {id:"history",name: "История изменений"},
        {id:"delete",name: "Удалить из истории записей"},
    ]

    return (<div className="content">
        <LeftBar tabs={tabs} activeTab={activeTab} setActiveTab={setActiveTab}/>
        <WarehouseContent activeTab={activeTab}/>
    </div>)
}
export {Warehouse}