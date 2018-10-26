/*
 * Intake, a command processing library
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) Intake team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package app.ashcon.intake.fluent;

import app.ashcon.intake.CommandCallable;
import app.ashcon.intake.dispatcher.Dispatcher;
import app.ashcon.intake.dispatcher.SimpleDispatcher;
import app.ashcon.intake.parametric.ParametricBuilder;

public abstract class AbstractDispatcherNode {

    protected final CommandGraph graph;
    protected final AbstractDispatcherNode parent;
    protected final Dispatcher dispatcher;

    /**
     * Create a new instance.
     *
     * @param graph      the root fluent graph object
     * @param parent     the parent node, or null
     * @param dispatcher the dispatcher for this node
     */
    protected AbstractDispatcherNode(CommandGraph graph, AbstractDispatcherNode parent, Dispatcher dispatcher) {
        this.graph = graph;
        this.parent = parent;
        this.dispatcher = dispatcher;
    }

    /**
     * Register a command with this dispatcher.
     *
     * @param callable the executor
     * @param aliases  the list of aliases, where the first alias is the primary one
     */
    public void register(CommandCallable callable, String... aliases) {
        dispatcher.registerCommand(callable, aliases);
    }

    /**
     * Build and register a command with this dispatcher using the
     * {@link ParametricBuilder} assigned on the root {@link CommandGraph}.
     *
     * @param object the object provided to the {@link ParametricBuilder}
     * @return this object
     * @see ParametricBuilder#registerMethodsAsCommands(Dispatcher, Object)
     */
    public AbstractDispatcherNode registerMethods(Object object) {
        graph.getBuilder().registerMethodsAsCommands(getDispatcher(), object);
        return this;
    }

    /**
     * Create a new command that will contain sub-commands.
     *
     * <p>The object returned by this method can be used to add sub-commands. To
     * return to this "parent" context, use {@link DispatcherNode#graph()}.</p>
     *
     * @param aliases the list of aliases, where the first alias is the primary one
     * @return an object to place sub-commands
     */
    public DispatcherNode group(String... aliases) {
        SimpleDispatcher command = new SimpleDispatcher();
        getDispatcher().registerCommand(command, aliases);
        return new DispatcherNode(graph, this, command);
    }

    /**
     * Return the parent node.
     *
     * @return the parent node
     * @throws RuntimeException if there is no parent node.
     */
    public AbstractDispatcherNode parent() {
        if (parent != null) {
            return parent;
        }

        throw new RuntimeException("This node does not have a parent");
    }

    /**
     * Get the root command graph.
     *
     * @return the root command graph
     */
    public CommandGraph graph() {
        return graph;
    }

    /**
     * Get the underlying dispatcher of this object.
     *
     * @return the dispatcher
     */
    public Dispatcher getDispatcher() {
        return dispatcher;
    }
}
