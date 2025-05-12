import {SpecViewAll} from "../../spec/Content/Tabs/SpecViewAll";
import {SpecCreateNew} from "../../spec/Content/Tabs/SpecCreateNew";
import {SpecChange} from "../../spec/Content/Tabs/SpecChange";
import {OrderChange} from "./Tabs/OrderChange";
import {OrderAdd} from "./Tabs/OrderAdd";
import {OrderAll} from "./Tabs/OrderAll";
import {OrderDelete} from "./Tabs/OrderDelete";

function OrderContent({activeTab}){
    const content = {
        all:<OrderAll/>,
        add:<OrderAdd/>,
        change: <OrderChange/>,
        delete: <OrderDelete/>
    };
    return (<div className='tab-content'>
        {content[activeTab]}
    </div>)
}
export {OrderContent}