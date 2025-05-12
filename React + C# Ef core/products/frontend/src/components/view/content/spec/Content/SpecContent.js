import {SpecViewAll} from "./Tabs/SpecViewAll";
import {SpecCreateNew} from "./Tabs/SpecCreateNew";
import {SpecChange} from "./Tabs/SpecChange";
import {SpecCount} from "./Tabs/SpecCount";
import {SpecDelete} from "./Tabs/SpecDelete";

function SpecContent({activeTab}) {
    const content = {
        all:<SpecViewAll/>,
        count:<SpecCount/>,
        add:<SpecCreateNew/>,
        change:<SpecChange/>,
        delete:<SpecDelete/>
    };
    return (<div className='tab-content'>
        {content[activeTab]}
    </div>)
}
export {SpecContent}