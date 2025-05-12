import {SpecApi} from "../../../../../utils/SpecApi";
import {useEffect, useState} from "react";

const SpecTreeNode = ({ node }) => {
    const [expanded, setExpanded] = useState(false);
    const hasChildren = node.specifications && node.specifications.length > 0;

    return (
        <div className="tree-node">
            <div
                className="node-content"
                onClick={() => setExpanded(!expanded)}
            >
                {hasChildren && (
                    <span className="toggle-icon">
            {expanded ? '▼' : '▶'}
          </span>
                )}
                <span className="node-name">
          {node.name} (x{node.count})
        </span>
                <span className="node-id">ID:{node.id}  Level: {node.level}</span>
                <span className="node-id"></span>
            </div>

            {expanded && hasChildren && (
                <div className="children">
                    {node.specifications.map(child => (
                        <SpecTreeNode key={child.id} node={child} />
                    ))}
                </div>
            )}
        </div>
    );
};
function  SpecViewAll() {

    const { data, error, loading, getAll  } = SpecApi();
    useEffect(() => {
        getAll();
    },[getAll]);
    if (loading) return (<p>чел, грузится</p>)
    if (error) return (<h2>Ошибка: {error.message}</h2>)
    return (
        <div className="spec-tree-container">
            <h2>Иерархия спецификаций</h2>
            <div className="spec-tree">
                {data.map(rootNode => (
                    <SpecTreeNode key={rootNode.id} node={rootNode} />
                ))}
            </div>
        </div>

    );
}
export {SpecViewAll}