import {useState} from "react";
import {LeftBar} from "../../LeftBar";
import {SpecContent} from "../../spec/Content/SpecContent";
import {SpecViewAll} from "../../spec/Content/Tabs/SpecViewAll";
import {SpecCreateNew} from "../../spec/Content/Tabs/SpecCreateNew";
import {SpecChange} from "../../spec/Content/Tabs/SpecChange";
import {WhAllOnDate} from "./Tabs/WhAllOnDate";
import {WhCheck} from "./Tabs/WhCheck";
import {WhChange} from "./Tabs/WhChange";
import {WhDelete} from "./Tabs/WhDelete";
import {WhHistory} from "./Tabs/WhHistory";

function WarehouseContent({activeTab}) {
    const content = {
        all:<WhAllOnDate/>,
        check:<WhCheck/>,
        change:<WhChange/>,
        history:<WhHistory/>,
        delete:<WhDelete/>
    };
    return (<div className='tab-content'>
        {content[activeTab]}
    </div>)
}
export {WarehouseContent}