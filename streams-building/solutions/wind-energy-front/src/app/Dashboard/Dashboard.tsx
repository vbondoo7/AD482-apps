import React, { useEffect, useState } from 'react';
import { PageSection, Title } from '@patternfly/react-core';
import { WindTurbineCard } from '../components/WindTurbineCard';
import { getWindTurbines } from "../services/WindTurbinesService";
import { WindTurbine } from '../models/WindTurbine';
import { WindTurbineProduction } from '../models/WindTurbineProduction';
import { WindTurbineStats } from '@app/models/WindTurbineStats';


interface ProductionByTurbine {
    [turbineId: number]: number
}

interface StatsByTurbine {
    [turbineId: number]: WindTurbineStats
}


export function Dashboard(): JSX.Element {
    const [turbines, setTurbines] = useState<WindTurbine[]>([]);
    const [powerProductionValues, setPowerProductionValues] = useState<ProductionByTurbine>({});
    const [stats, setStats] = useState<StatsByTurbine>({});


    useEffect(() => {
        getWindTurbines().then((turbines) => {
            setTurbines(turbines);
            getPowerServerEvents();
            getPowerStatsEvents();
        });
    }, []);

    function getPowerServerEvents() {
        const eventSource = new EventSource("http://localhost:8080/turbines/generated-power");
        eventSource.onmessage = (message) => {
            const production: WindTurbineProduction = JSON.parse(message.data);
            setPowerProductionValues(values => ({
                ...values,
                [production.turbineId]: production.megawatts
            }));
        };
    }

    function getPowerStatsEvents() {
        const eventSource = new EventSource("http://localhost:8080/turbines/measurements-count");
        eventSource.onmessage = (message) => {
            const turbineStats: WindTurbineStats = JSON.parse(message.data);
            setStats(stats => ({
                ...stats,
                [turbineStats.turbineId]: turbineStats
            }));
        };
    }

    function getTurbineProduction(id: number) {
        const production = powerProductionValues[id];
        return production ? production : 0;
    }

    function getTurbineStats(id: number) {
        const turbineStats = stats[id];
        return turbineStats || { count: 0 };
    }

    return (
        <PageSection>
            <Title headingLevel="h1" size="lg">Dashboard Page Title!</Title>
            {turbines.map(turbine =>
                <WindTurbineCard
                    key={turbine.id}
                    turbine={turbine}
                    production={{ megawatts: getTurbineProduction(turbine.id) }}
                    stats={getTurbineStats(turbine.id)}
                ></WindTurbineCard>
            )}
        </PageSection>
    )
}

